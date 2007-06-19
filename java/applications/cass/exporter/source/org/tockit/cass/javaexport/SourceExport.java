package org.tockit.cass.javaexport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class SourceExport {
	public static void exportSource(IJavaProject javaProject,
			String targetLocation) throws JavaModelException,
			ClassNotFoundException, SQLException {
		String className = "org.hsqldb.jdbcDriver"; // path of driver class
		Class.forName(className); // Load the Driver
		String DB_URL = "jdbc:hsqldb:file:" + targetLocation + "/jenaDB"; // URL
																			// of
																			// database
		String DB_USER = "sa"; // database user id
		String DB_PASSWD = ""; // database password
		String DB = "HSQL"; // database type

		// Create database connection
		IDBConnection conn = new DBConnection(DB_URL, DB_USER, DB_PASSWD, DB);
		conn.cleanDB(); // it doesn't seem to work without this -- TODO: figure
						// out why
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);

		// create or open the default model
		Model model = maker.createDefaultModel();
		extractBaseAssertions(javaProject, model);

		// load rules
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						SourceExport.class
								.getResourceAsStream("InferenceRules")));
		List rules = Rule.parseRules(Rule.rulesParserFromReader(reader));
		
		// create inferred model
		Reasoner reasoner = new GenericRuleReasoner(rules);
		InfModel infModel = ModelFactory.createInfModel(reasoner, model);
		Iterator it = infModel.listStatements(model.createResource("test.testing.Test.main(..)"), null, (RDFNode)null);
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		it = infModel.listStatements(model.createResource("test.testing.Test.printHelloWorld(..)"), null, (RDFNode)null);
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		// join the inferred data into the persisted model (it seems Jena can't persist inferred statements
		// directly)
		model.add(infModel);
		
		// shutdown hsqldb
		conn.getConnection().createStatement().execute("SHUTDOWN;");

		// Close the database connection
		conn.close();
	}

	private static void extractBaseAssertions(IParent parent, final Model model)
			throws JavaModelException {
		if (parent instanceof IPackageFragment) {
			IPackageFragment packageFragment = (IPackageFragment) parent;
			model.createResource(packageFragment.getElementName()).addProperty(
					Properties.TYPE, Types.PACKAGE);
		}
		for (int i = 0; i < parent.getChildren().length; i++) {
			final IJavaElement element = parent.getChildren()[i];
			if (parent instanceof IPackageFragment) {
				IPackageFragment packageFragment = (IPackageFragment) parent;
				model.createResource(packageFragment.getElementName())
						.addProperty(Properties.CONTAINS,
								element.getElementName());
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
						getTop().addProperty(Properties.CONTAINS, currentRes);
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
						getTop().addProperty(
								Properties.CALLS,
								model.createResource(typeBinding.getQualifiedName() + "."
										+ methodBinding.getName() + "(..)"));
						return true;
					}
				});
			}
			if (element instanceof IParent) {
				if (!element.getElementName().endsWith("jar")) {
					extractBaseAssertions((IParent) element, model);
				}
			}
		}
	}
}