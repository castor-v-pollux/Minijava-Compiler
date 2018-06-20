package j2p;

import java.io.InputStream;

import parser.MiniJavaParser;
import parser.ParseException;
import piglet.PAllocate;
import piglet.PBinOp;
import piglet.PCJumpStmt;
import piglet.PCall;
import piglet.PCode;
import piglet.PErrorStmt;
import piglet.PExp;
import piglet.PGoal;
import piglet.PIdentifier;
import piglet.PInteger;
import piglet.PJumpStmt;
import piglet.PLabel;
import piglet.PLoadStmt;
import piglet.PMoveStmt;
import piglet.PNoOpStmt;
import piglet.PPrintStmt;
import piglet.PProcedure;
import piglet.PStmt;
import piglet.PStmtExp;
import piglet.PStmtList;
import piglet.PStoreStmt;
import piglet.PTemp;
import symbol.MClass;
import symbol.MIdentifier;
import symbol.MMethod;
import symbol.MType;
import symbol.MVar;
import symbol.SymbolTable;
import syntaxtree.Node;
import visitor.ClassVisitor;
import visitor.MemberVisitor;
import visitor.TranslateVisitor;

/**
 * <p>该类为第二次作业J2P的主类。
 * <p>以下对整个程序的架构、翻译过程和Piglet语言缩进规范进行说明：
 * 
 * <p>本程序沿袭了第一次作业中对MiniJava符号系统的封装，将MiniJava中的标识符抽象为以下类：
 * <ul>
 * <li>{@link MIdentifier}    标识符的父类，记录名称、所在行列
 * <li>|---{@link MClass}     表示一个类，维护其方法、变量列表等
 * <li>|---{@link MMethod}    表示一个方法，维护其参数、变量列表等
 * <li>|---{@link MVar}       表示一个变量，维护其类型
 * </ul>
 * <p>另外将MiniJava中的类型抽象为以下接口：
 * <ul>
 * <li>{@link MType}                 类型的父接口，方法包括{@link MType#getName()}和{@link MType#canConvertTo(MType)}
 * <li>|---{@link MClass}            类作为引用类型的承载者，继承该接口
 * <li>|---{@link MType#IntType}     接口的常量实现，表示{@code int}类型
 * <li>|---{@link MType#BooleanType} 接口的常量实现，表示{@code boolean}类型
 * <li>|---{@link MType#ArrayType}   接口的常量实现，表示{@code int[]}类型
 * <li>|---{@link MType#OtherType}   接口的常量实现，表示{@code String[]}类型
 * </ul>
 * <p>和第一次作业相同，这样的封装可以十分优雅地建立起符号表，对于字符串的操作只在构建语法树的底层进行，耦合性很低。
 * 
 * <p>另外，为了生成的Piglet代码有很高的可读性，以下定义了一套关于Piglet语句的缩进规范：
 * <ul>
 * <li>对于Goal和StmtExp中的StmtList，须缩进一个Tab宽度，若StmtList中某语句前有Label，则Label左端与缩进前对齐。
 * <li>一个Piglet表达式若跨越多行，每一行必须与上一行开头对齐，即整个表达式在代码中应处于一个矩形区域内。
 * <li>一个Piglet语句有多个参数时，若全为单行参数，则写在一行内，否则对于每一个多行的参数，其后面一个参数应与第一个参数开头对齐，这样保证了语句的参数列表处于一个矩形区域内。
 * </ul>
 *
 * <p>本程序生成的Piglet程序全部遵循该规范，为此，对Piglet代码进行了以下封装：
 * <ul>
 * <li>{@link PCode}                Piglet代码的抽象基类，定义了Piglet代码打印的基本规范
 * <li>|---{@link PExp}             Piglet表达式的抽象基类，定义了Piglet表达式打印的基本规范
 * <li>|---|---{@link PAllocate}    HAllocate
 * <li>|---|---{@link PBinOp}       BinOp
 * <li>|---|---{@link PCall}        Call
 * <li>|---|---{@link PIdentifier}  标识符，用来将函数名看做地址
 * <li>|---|---{@link PInteger}     整数
 * <li>|---|---{@link PStmtExp}     StmtExp
 * <li>|---|---{@link PTemp}        Temp
 * <li>|---{@link PGoal}            Goal
 * <li>|---{@link PProcedure}       Procedure
 * <li>|---{@link PStmt}            Piglet语句的抽象基类，定义了Piglet语句打印的基本规范
 * <li>|---|---{@link PCJumpStmt}   CJump
 * <li>|---|---{@link PErrorStmt}   Error
 * <li>|---|---{@link PJumpStmt}    Jump
 * <li>|---|---{@link PLabel}       Label
 * <li>|---|---{@link PLoadStmt}    HLoad
 * <li>|---|---{@link PMoveStmt}    Move
 * <li>|---|---{@link PNoOpStmt}    Noop
 * <li>|---|---{@link PPrintStmt}   Print
 * <li>|---|---{@link PStoreStmt}   HStore
 * <li>|---{@link PStmtList}        StmtList
 * </ul>
 * <p>每一个Piglet代码封装类中都有print方法可以在指定缩进位置打印该代码段，在具体的类中各自根据规范实现print方法，即可实现Piglet程序的完美缩进。
 * 
 * <p>本程序主要分为五步，使用了三个Visitor进行遍历，前三步与第一次作业相同，具体如下：
 * <ul>
 * <li>1.使用{@link ClassVisitor}，遍历各个类的声明部分，将这些类加入符号表中
 * <li>2.使用{@link MemberVisitor}，遍历各个类的成员变量和方法，以及各个方法的局部变量，将它们加入到符号表中
 * <li>3.使用{@link SymbolTable#checkClassExtension()}，在符号表内部检查类的继承关系，在使用拓扑排序确定继承关系无环后，根据拓扑顺序进行类成员变量和方法的继承
 * <li>4.使用{@link TranslateVisitor}，遍历各个方法中的语句列表，将其翻译成{@link PCode}代码对象
 * <li>5.对最终得到的{@code PCode}对象进行打印
 * </ul>
 * 
 * <p>在代码的翻译中有如下注意点：
 * <ul>
 * <li>关于原MiniJava中类型的处理：
 * 		<ul>
 * 		<li>int,boolean都看做Piglet中的整数，其中true看做1，false看做0
 * 		<li>int[]在Piglet中使用一个地址来表示，该地址指向一块被分配的空间，开头四字节是数组长度n，后面4n字节是数组的实际内容
 * 		<li>类实例在Piglet中使用一个地址来表示，该地址指向一块被分配的空间，开头四字节是一个地址，指向该实例的类的方法表，后面内容是该实例的属性表
 * 		</ul>
 * <li>关于方法多态和属性覆盖：
 * 		<ul>
 * 		<li>由于子类完全继承父类的方法，因此让子类方法表的前面部分与父类方法表一致，若有重载的方法，替换相应的方法地址即可，子类方法表的后面部分是子类所增加的方法。
 * 		<li>由于子类属性仅仅覆盖父类属性，在调用父类方法时仍然可能用到父类属性，因此对于子类和父类的重名属性，不能进行替换，而是对子类所有属性都开辟额外的空间。
 * 		</ul>
 * <li>关于变量初始化：
 * 		<ul>
 * 		<li>类的成员变量在实例化该类时全部初始化为0
 * 		<li>方法的局部变量在进入该方法时全部初始化为0
 * 		<li>数组的内容在分配该数组时全部初始化为0
 * 		</ul>
 * <li>关于具体语句的翻译：
 * 		<ul>
 * 		<li>在使用引用型变量（包括类和数组）时，需要先检查非空
 * 		<li>在对数组进行下标操作时，需要检查数组是否越界
 * 		<li>在新建数组时，数组长度必须非负
 * 		<li>对于&&表达式的处理，当左操作数为0时，不计算右操作数（短路与）
 * 		</ul>
 * </ul>
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
		SymbolTable.performClassExtension();
		PCode code = (PCode) goal.accept(new TranslateVisitor(), null);
		code.print(0);
	}

}
