package minijava;

import java.io.InputStream;

import parser.MiniJavaParser;
import parser.ParseException;
import symbol.MClass;
import symbol.MIdentifier;
import symbol.MMethod;
import symbol.MType;
import symbol.MVar;
import symbol.SymbolTable;
import syntaxtree.Identifier;
import syntaxtree.Node;
import visitor.ClassVisitor;
import visitor.MemberVisitor;
import visitor.TypeCheckVisitor;

/**
 * <p>该类为第一次作业TypeCheck的主类。
 * <p>以下对整个程序的架构、分析过程和所检查的类型错误进行说明：
 * 
 * <p>本程序对MiniJava的符号系统进行了（使类型检查得以）非常优雅的封装和组织，将MiniJava中的标识符抽象为以下类：
 * <ul>
 * <li>{@link MIdentifier}    标识符的父类，记录名称、所在行列
 * <li>|---{@link MClass}     表示一个类，维护其方法、变量列表等
 * <li>|---{@link MMethod}    表示一个方法，维护其参数、变量列表等
 * <li>|---{@link MVar}       表示一个变量，维护其类型
 * </ul>
 * 
 * <p>另外将MiniJava中的类型抽象为以下接口：
 * <ul>
 * <li>{@link MType}                 类型的父接口，方法包括{@link MType#getName()}和{@link MType#canConvertTo(MType)}
 * <li>|---{@link MClass}            类作为引用类型的承载者，继承该接口
 * <li>|---{@link MType#IntType}     接口的常量实现，表示{@code int}类型
 * <li>|---{@link MType#BooleanType} 接口的常量实现，表示{@code boolean}类型
 * <li>|---{@link MType#ArrayType}   接口的常量实现，表示{@code int[]}类型
 * <li>|---{@link MType#OtherType}   接口的常量实现，表示{@code String[]}类型
 * </ul>
 * <p>在这样的抽象与封装下，我们可以非常轻松地建立起MiniJava的符号表并对其进行类型检查，而不需要在高层对字符串进行任何操作。程序中{@link SymbolTable}类为单例类，负责维护整个符号表。
 * 
 * <p>本程序类型检查主要分为四步，使用了三个Visitor进行遍历，具体如下：
 * <ul>
 * <li>1.使用{@link ClassVisitor}，遍历各个类的声明部分，将这些类加入符号表中
 * <li>2.使用{@link MemberVisitor}，遍历各个类的成员变量和方法，以及各个方法的局部变量，将它们加入到符号表中
 * <li>3.使用{@link SymbolTable#checkClassExtension()}，在符号表内部检查类的继承关系，在使用拓扑排序确定继承关系无环后，根据拓扑顺序进行类成员变量和方法的继承
 * <li>4.使用{@link TypeCheckVisitor}，遍历各个方法中的语句列表，利用建好的符号表进行所有语句和表达式的类型检查
 * </ul>
 * <p>为什么建立符号表使用两个Visitor而不是一个呢?虽然第一个Visitor只对类名进行了遍历并且加入符号表中，但这保证了第二次遍历时，所有用到的类都已经在符号表中，包括变量声明的类型，继承的父类等，可以直接引用到对应的类上。
 * <p>如果将两次遍历合并，那么在声明一个变量时，这个类型可能在后面才会声明，使得我们不得不用字符串来标记尚未声明的类，在将符号表建立完成后再使用这些字符串去检查类型、继承等，提高了程序的耦合性，与我们对符号表面向对象的设计相悖。
 * <p>这样的设计，整个三个Visitor中对字符串的显式操作只发生在对{@link Identifier}节点访问中，高层的类型检查全部对于{@link MIdentifier}的类型{@link MType}进行，实现了高度的封装与解耦。
 * 
 * <p>本程序对于所要求的六类类型检查全部完成，具体分别分散在四个步骤中，关于每种错误的检查在哪一步中进行，可参考以下文档：
 * <ul>
 * <li>{@link ClassVisitor}
 * <li>{@link MemberVisitor}
 * <li>{@link SymbolTable#checkClassExtension()}
 * <li>{@link TypeCheckVisitor}
 * </ul>
 * 
 * <p>本程序未进行数组越界和变量初始化的检查，因为这样做会涉及到对程序控制流和表达式取值等的静态分析，十分复杂且会影响当前程序对类型检查优雅的实现:(
 * 
 * @author castor_v_pollux
 */
public class Main {

	public static void main(String[] args) {
		InputStream in = System.in;
		// try {
		// in = new FileInputStream(args[0]);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// return;
		// }
		Node goal;
		try {
			goal = new MiniJavaParser(in).Goal();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return;
		}
		goal.accept(new ClassVisitor());
		goal.accept(new MemberVisitor(), null);
		SymbolTable.checkClassExtension();
		goal.accept(new TypeCheckVisitor(), null);
		System.out.println("Program type checked successfully");
	}

}