package org.tockit.cass.javaexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class SourceExportJob extends Job {
	private static final int ERROR_CODE_JAVA_MODEL_EXCEPTION = 1;
	private static final int ERROR_CODE_FILE_NOT_FOUND_EXCEPTION = 2;
	private static final String PLUGIN_NAME = "org.tockit.cass.sourceexport";
	private IProgressMonitor progressMonitor;
	private final IJavaProject javaProject;
	private final File targetFile;
	private final String fileFormat;
	private final Pattern[] ignorePattern;

	public SourceExportJob(IJavaProject javaProject,
			File targetFile, String fileFormat, String[] ignorePattern) {
		super("Export Java source as graph");
		this.javaProject = javaProject;
		this.targetFile = targetFile;
		this.fileFormat = fileFormat;
		this.ignorePattern = new Pattern[ignorePattern.length];
		for (int i = 0; i < ignorePattern.length; i++) {
			this.ignorePattern[i] = Pattern.compile(ignorePattern[i]);
		}
		this.setUser(true);
		this.setRule(javaProject.getSchedulingRule());
	}

	public boolean exportSource() throws JavaModelException, FileNotFoundException {
		progressMonitor.beginTask("Exporting Java source graph", 3);
		Model model = ModelFactory.createDefaultModel();

		progressMonitor.subTask("Extract base data from Java code");
		boolean completed = extractAssertions(javaProject, model);
		if(!completed) {
			return false;
		}
		progressMonitor.worked(1);

		progressMonitor.subTask("Inferring extra relations");
		completed = addExtraAssertions(model);
		if(!completed) {
			return false;
		}
		progressMonitor.worked(1);
		
		progressMonitor.subTask("Writing output file");
		model.setNsPrefixes(Namespaces.PREFIX_MAPPING);
		model.write(new FileOutputStream(targetFile),fileFormat);
		progressMonitor.done();

		return true;
	}

	private boolean addExtraAssertions(Model model) {
		// we write statements collected after iteration is complete to avoid that annoying
		// ConcurrentModificationException, so store new pairs which will be in the relation
		// we are creating as two-element Resource[]
		List newPairs = new ArrayList();

		// add extended callgraph
		Iterator it = model.listStatements(null, Properties.CALLS_CLOSURE,
				(RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			Resource subject = stmt.getSubject();
			Resource object = (Resource) stmt.getObject();
			newPairs.add(new Resource[]{subject,object});
			Iterator it2 = model.listStatements(null, Properties.CONTAINS_CLOSURE, subject);
			while (it2.hasNext()) {
				Statement contSubjStmt = (Statement) it2.next();
				Iterator it3 = model.listStatements(null, Properties.CONTAINS_CLOSURE, object);
				while (it3.hasNext()) {
					Statement contObjStmt = (Statement) it3.next();
					newPairs.add(new Resource[]{contSubjStmt.getSubject(),contObjStmt.getSubject()});
				}
			}
			if(progressMonitor.isCanceled()) {
				return false;
			}
		}
		for (Iterator npIter = newPairs.iterator(); npIter.hasNext();) {
			Resource[] resources = (Resource[]) npIter.next();
			resources[0].addProperty(Properties.CALLS_EXTENDED, resources[1]);
		}
		
		// add combined dependency graph
		newPairs = new ArrayList();
		it = model.listStatements(null, Properties.EXTENDS_CLOSURE,
				(RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			Resource subject = stmt.getSubject();
			Resource object = (Resource) stmt.getObject();
			newPairs.add(new Resource[]{subject,object});
			Iterator it2 = model.listStatements(object, Properties.IMPLEMENTS_CLOSURE, (RDFNode) null);
			while (it2.hasNext()) {
				Statement implStmt = (Statement) it2.next();
				newPairs.add(new Resource[]{subject,(Resource) implStmt.getObject()});
			}
			if(progressMonitor.isCanceled()) {
				return false;
			}
		}
		for (Iterator npIter = newPairs.iterator(); npIter.hasNext();) {
			Resource[] resources = (Resource[]) npIter.next();
			resources[0].addProperty(Properties.DERIVED_FROM_CLOSURE, resources[1]);
		}
		it = model.listStatements(null, Properties.IMPLEMENTS_CLOSURE,
				(RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(Properties.DERIVED_FROM_CLOSURE, stmt.getObject());
		}
		
		// add generic dependency graph
		it = model.listStatements(null, Properties.CALLS_EXTENDED,
				(RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(Properties.DEPENDS_TRANSITIVELY,
					stmt.getObject());
		}
		// TODO: the next ones should include upset in containment similar to 
		// CALLS_EXTENDED maybe do union first, then go up containment hierarchies
		it = model.listStatements(null, Properties.HAS_PARAMETER_EXTENDED,
				(RDFNode) null); 
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(Properties.DEPENDS_TRANSITIVELY,
					stmt.getObject());
		}
		it = model.listStatements(null, Properties.HAS_RETURN_TYPE_EXTENDED,
				(RDFNode) null); 
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(Properties.DEPENDS_TRANSITIVELY,
					stmt.getObject());
		}
		it = model.listStatements(null, Properties.HAS_FIELD_TYPE_EXTENDED,
				(RDFNode) null); 
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(Properties.DEPENDS_TRANSITIVELY,
					stmt.getObject());
		}
		it = model.listStatements(null, Properties.DERIVED_FROM_CLOSURE,
				(RDFNode) null); 
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(Properties.DEPENDS_TRANSITIVELY,
					stmt.getObject());
		}

		return true;
	}

	private boolean extractAssertions(IParent parent, final Model model)
			throws JavaModelException {
		if(progressMonitor.isCanceled()) {
			return false; // note that just checking here implies finishing
			// the whole stack, but that shouldn't make much difference
		}
		Resource packageResource = null;
		if (parent instanceof IPackageFragment) {
			packageResource = createResource(model, (IPackageFragment) parent);
		}
		mainLoop: for (int i = 0; i < parent.getChildren().length; i++) {
			IJavaElement element = parent.getChildren()[i];
			for (int j = 0; j < ignorePattern.length; j++) {
				Pattern pattern = ignorePattern[j];
				if(pattern.matcher(element.getElementName()).matches()) {
					continue mainLoop;
				}
			}
			if (element instanceof ICompilationUnit) {
				ICompilationUnit compilationUnit = (ICompilationUnit) element;
				ASTParser parser = ASTParser.newParser(AST.JLS3);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setSource(compilationUnit);
				parser.setResolveBindings(true);
				ASTNode result = parser.createAST(null);
				final Resource elementResource = createResource(model, element);
				if (packageResource != null) {
					addPropertyWithTransitiveClosure(model, packageResource,
							elementResource, Properties.CONTAINS,
							Properties.CONTAINS_CLOSURE);
				}
				result.accept(new ASTVisitor() {
					List resources = new ArrayList() {
						{
							add(elementResource);
						}
					};

					private Resource getTop() {
						return ((Resource) resources.get(resources.size() - 1));
					}

					private void pushOnStack(Resource currentRes) {
						addPropertyWithTransitiveClosure(model, getTop(),
								currentRes, Properties.CONTAINS,
								Properties.CONTAINS_CLOSURE);
						resources.add(currentRes);
					}

					private void popStack() {
						resources.remove(resources.size() - 1);
					}

					public boolean visit(TypeDeclaration node) {
						ITypeBinding typeBinding = node.resolveBinding();
						Resource currentRes = createResource(model, typeBinding);
						pushOnStack(currentRes);
						return true;
					}

					public void endVisit(TypeDeclaration node) {
						popStack();
					}

					public boolean visit(MethodDeclaration node) {
						Resource currentRes = createResource(model, node.resolveBinding());
						pushOnStack(currentRes);
						return true;
					}

					public void endVisit(MethodDeclaration node) {
						popStack();
					}

					public boolean visit(MethodInvocation node) {
						addPropertyWithTransitiveClosure(model, getTop(),
								createResource(model, node.resolveMethodBinding()), Properties.CALLS,
								Properties.CALLS_CLOSURE);
						return true;
					}
				});
			}
			if (element instanceof IParent) {
				extractAssertions((IParent) element, model);
			}
		}
		return true;
	}

	private static Resource createResource(final Model model, IJavaElement element) {
		// TODO: path contains package fragement root but shouldn't
		final Resource elementRes = model.createResource(Namespaces.COMPILATION_UNITS + encodeForURI(element.getPath().toString()));
		elementRes.addProperty(Properties.TYPE, Types.COMPILATION_UNIT);
		return elementRes;
	}

	private static String encodeForURI(String unescapedString) {
		try {
			return URLEncoder.encode(unescapedString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding 'UTF-8' not available, but should be");
		}
	}

	private static Resource createResource(Model model, IPackageBinding packageBinding) {
		return createPackageResource(model, packageBinding.getName());
	}

	private static Resource createPackageResource(Model model, String packageName) {
		Resource packageResource = model.createResource(Namespaces.PACKAGES + 
				encodeForURI(packageName));
		if (!model.containsResource(packageResource)) {
			packageResource.addProperty(Properties.TYPE, Types.PACKAGE);
			packageResource.addProperty(Properties.CONTAINS_CLOSURE, packageResource);
			int lastDot = packageName.lastIndexOf('.');
			if (lastDot != -1) {
				Resource parentPackageResource = createPackageResource(model,
						packageName.substring(0, lastDot));
				addPropertyWithTransitiveClosure(model, parentPackageResource,
						packageResource, Properties.CONTAINS,
						Properties.CONTAINS_CLOSURE);
			}
		}		
		return packageResource;
	}

	private static Resource createResource(final Model model, IPackageFragment packageFragment) {
		return createPackageResource(model, packageFragment.getElementName());
	}

	private static void addPropertyWithTransitiveClosure(Model model,
			Resource from, Resource to, Property coveringRelation,
			Property closureRelation) {
		from.addProperty(coveringRelation, to);
		from.addProperty(closureRelation, to);
		// for all X: add (from,X) if (to,X)
		Iterator it = model.listObjectsOfProperty(to, closureRelation);
		while (it.hasNext()) {
			from.addProperty(closureRelation, (Resource) it.next());
		}
		// for all X: add (X,to) if (X,from)
		it = model.listStatements(null, closureRelation, from);
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(closureRelation, to);
		}
	}

	private static Resource createResource(final Model model, ITypeBinding typeBinding) {
		Resource typeRes = model.createResource(Namespaces.TYPES
				+ encodeForURI(typeBinding.getKey()));
		if (!model.containsResource(typeRes)) {
			typeRes.addProperty(Properties.CONTAINS_CLOSURE, typeRes);
			typeRes.addProperty(Properties.EXTENDS_CLOSURE, typeRes);
			if(typeBinding.getPackage() != null) {
				Resource packageRes = createResource(model, typeBinding.getPackage());
				addPropertyWithTransitiveClosure(model, packageRes, typeRes, 
						Properties.CONTAINS, Properties.CONTAINS_CLOSURE);
			}
			typeRes.addProperty(Properties.TYPE, Types.TYPE);
			if (typeBinding.isInterface()) {
				typeRes.addProperty(Properties.TYPE, Types.INTERFACE);
			} else {
				typeRes.addProperty(Properties.TYPE, Types.CLASS);
			}
			ITypeBinding superClass = typeBinding.getSuperclass();
			if(superClass != null) {
				addPropertyWithTransitiveClosure(model, typeRes, createResource(model, superClass), 
						Properties.EXTENDS, Properties.EXTENDS_CLOSURE);
			}
			ITypeBinding[] interfaces = typeBinding.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				ITypeBinding superInterface = interfaces[i];
				addPropertyWithTransitiveClosure(model, typeRes, createResource(model, superInterface), 
						Properties.IMPLEMENTS, Properties.IMPLEMENTS_CLOSURE);
			}
			IVariableBinding[] fields = typeBinding.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				IVariableBinding field = fields[i];
				Resource fieldTypeResource = createResource(model, field
						.getType());
				typeRes.addProperty(Properties.HAS_FIELD_TYPE,
						fieldTypeResource);
				typeRes.addProperty(Properties.HAS_FIELD_TYPE_EXTENDED,
						fieldTypeResource);
				if (field.getType().isArray()) {
					typeRes.addProperty(Properties.HAS_FIELD_TYPE_EXTENDED,
							createResource(model, field.getType()
									.getElementType()));
				}
			}
		}		
		return typeRes;
	}

	private static Resource createResource(final Model model, IMethodBinding methodBinding) {
		Resource methodResource = model.createResource(Namespaces.METHODS + encodeForURI(methodBinding.getKey()));
		if(!model.containsResource(methodResource)) {
			methodResource.addProperty(Properties.CALLS_CLOSURE, methodResource);
			methodResource.addProperty(Properties.CONTAINS_CLOSURE, methodResource);
			ITypeBinding[] formalParams = methodBinding.getParameterTypes();
			for (int i = 0; i < formalParams.length; i++) {
				ITypeBinding param = formalParams[i];
				Resource paramResource = createResource(model, param);
				methodResource.addProperty(Properties.HAS_PARAMETER, paramResource);
				methodResource.addProperty(Properties.HAS_PARAMETER_EXTENDED, paramResource);
				if(param.isArray()) {
					methodResource.addProperty(Properties.HAS_PARAMETER_EXTENDED, createResource(model, param.getElementType()));
				}
			}
			ITypeBinding retVal = methodBinding.getReturnType();
			Resource retValResource = createResource(model, retVal);
			methodResource.addProperty(Properties.HAS_RETURN_TYPE, retValResource);
			methodResource.addProperty(Properties.HAS_RETURN_TYPE_EXTENDED, retValResource);
			if(retVal.isArray()) {
				methodResource.addProperty(Properties.HAS_RETURN_TYPE_EXTENDED, createResource(model, retVal.getElementType()));
			}
		}
		return methodResource;
	}

	protected IStatus run(IProgressMonitor monitor) {
		this.progressMonitor = monitor;
		try {
			boolean exportCompleted = exportSource();
			if (exportCompleted) {
				return Status.OK_STATUS;
			} else {
				// cancelled -- TODO do some cleanup
				return Status.CANCEL_STATUS;
			}			
		} catch (JavaModelException e) {
			return new Status(Status.ERROR, PLUGIN_NAME, ERROR_CODE_JAVA_MODEL_EXCEPTION, e.getLocalizedMessage(), e);
		} catch (FileNotFoundException e) {
			return new Status(Status.ERROR, PLUGIN_NAME, ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, e.getLocalizedMessage(), e);
		}
	}
}