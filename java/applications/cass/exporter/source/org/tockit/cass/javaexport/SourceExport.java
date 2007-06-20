package org.tockit.cass.javaexport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class SourceExport {
	public static void exportSource(IJavaProject javaProject,
			String targetLocation) throws JavaModelException,
			ClassNotFoundException, SQLException {
		// set up DB connection
		String className = "org.hsqldb.jdbcDriver";
		Class.forName(className);
		String DB_URL = "jdbc:hsqldb:file:" + targetLocation + "/jenaDB";
		String DB_USER = "sa";
		String DB_PASSWD = "";
		String DB = "HSQL";

		// Create database connection
		IDBConnection conn = new DBConnection(DB_URL, DB_USER, DB_PASSWD, DB);
		conn.cleanDB(); // it doesn't seem to work without this -- TODO: figure
		// out why
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);

		// create or open the default model
		Model model = maker.createDefaultModel();

		// extract assertions into model
		extractAssertions(javaProject, model);
		
		// do some extra inferences beyond the ones done while adding
        addExtraAssertions(model);
        
		// shutdown hsqldb
		conn.getConnection().createStatement().execute("SHUTDOWN;");

		// Close the database connection
		conn.close();
	}

	private static void addExtraAssertions(Model model) {
		Iterator it = model.listStatements(null, Properties.CALLS_EXTENDED, (RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(Properties.DEPENDS_TRANSITIVELY, stmt.getObject());
		}
	}

	private static void extractAssertions(IParent parent, final Model model)
			throws JavaModelException {
		if (parent instanceof IPackageFragment) {
			IPackageFragment packageFragment = (IPackageFragment) parent;
			model.createResource(packageFragment.getElementName()).addProperty(
					Properties.TYPE, Types.PACKAGE);
		}
		for (int i = 0; i < parent.getChildren().length; i++) {
			IJavaElement element = parent.getChildren()[i];
			final Resource elementResource = model.createResource(element
					.getElementName());
			if (parent instanceof IPackageFragment) {
				IPackageFragment packageFragment = (IPackageFragment) parent;
				Resource packageResource = model.createResource(packageFragment
						.getElementName());
				addPropertyWithTransitiveClosure(model, packageResource,
						elementResource, Properties.CONTAINS,
						Properties.CONTAINS_TRANSITIVELY);
			}
			if (element instanceof ICompilationUnit) {
				ICompilationUnit compilationUnit = (ICompilationUnit) element;
				ASTParser parser = ASTParser.newParser(AST.JLS3);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setSource(compilationUnit);
				parser.setResolveBindings(true);
				ASTNode result = parser.createAST(null);
				final Resource elementRes = model.createResource(element
						.getElementName());
				elementRes.addProperty(Properties.TYPE, Types.COMPILATION_UNIT);
				result.accept(new ASTVisitor() {
					List resources = new ArrayList() {
						{
							add(elementRes);
						}
					};

					private Resource getTop() {
						return ((Resource) resources.get(resources.size() - 1));
					}

					private void pushOnStack(Resource currentRes) {
						addPropertyWithTransitiveClosure(model, getTop(),
								currentRes, Properties.CONTAINS,
								Properties.CONTAINS_TRANSITIVELY);
						resources.add(currentRes);
					}

					private void popStack() {
						resources.remove(resources.size() - 1);
					}

					public boolean visit(TypeDeclaration node) {
						ITypeBinding typeBinding = node.resolveBinding();
						Resource currentRes = model.createResource(typeBinding
								.getQualifiedName()); // TODO: figure out how
						// to get the package
						if (node.isInterface()) {
							currentRes.addProperty(Properties.TYPE,
									Types.INTERFACE);
						} else {
							currentRes
									.addProperty(Properties.TYPE, Types.CLASS);
						}
						pushOnStack(currentRes);
						return true;
					}

					public void endVisit(TypeDeclaration node) {
						popStack();
					}

					public boolean visit(MethodDeclaration node) {
						IMethodBinding methodBinding = node.resolveBinding();
						ITypeBinding typeBinding = methodBinding
								.getDeclaringClass();
						Resource currentRes = model.createResource(typeBinding
								.getQualifiedName()
								+ "." + methodBinding.getName() + "(..)");
						currentRes.addProperty(Properties.TYPE, Types.METHOD);
						pushOnStack(currentRes);
						return true;
					}

					public void endVisit(MethodDeclaration node) {
						popStack();
					}

					public boolean visit(MethodInvocation node) {
						IMethodBinding methodBinding = node
								.resolveMethodBinding();
						ITypeBinding typeBinding = methodBinding
								.getDeclaringClass();
						addPropertyWithTransitiveClosure(model, getTop(), model
								.createResource(typeBinding.getQualifiedName()
										+ "." + methodBinding.getName()
										+ "(..)"), Properties.CALLS,
								Properties.CALLS_TRANSITIVELY);
						return true;
					}
				});
			}
			if (element instanceof IParent) {
				if (!element.getElementName().endsWith("jar")) {
					extractAssertions((IParent) element, model);
				}
			}
		}
	}

	protected static void addPropertyWithTransitiveClosure(Model model,
			Resource from, Resource to, Property coveringRelation,
			Property closureRelation) {
		from.addProperty(coveringRelation, to);
		from.addProperty(closureRelation, to);
		// add all (from,X) if (to,X)
		Iterator it = model.listObjectsOfProperty(to, closureRelation);
		while (it.hasNext()) {
			from.addProperty(closureRelation, (Resource) it.next());
		}
		// add all (X,to) if (X,from)
		it = model.listStatements(null, closureRelation, from);
		while (it.hasNext()) {
			Statement stmt = (Statement) it.next();
			stmt.getSubject().addProperty(closureRelation, to);
		}
	}
}