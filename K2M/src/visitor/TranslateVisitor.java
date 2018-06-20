package visitor;

import kanga.Method;
import syntaxtree.ALoadStmt;
import syntaxtree.AStoreStmt;
import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.CallStmt;
import syntaxtree.ErrorStmt;
import syntaxtree.Goal;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.NodeOptional;
import syntaxtree.PassArgStmt;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.Reg;
import syntaxtree.SimpleExp;
import syntaxtree.SpilledArg;

/**
 * 本Visitor用来进行语言的翻译，由于除了管理栈，Kanga每一个语句在MIPS中都有唯一对应，因此不再详细注释。
 *
 * @author castor_v_pollux
 */
public class TranslateVisitor extends GJVoidDepthFirst<Method> {

	Reg arg;

	public void visit(NodeOptional n, Method argu) {
		if (n.present()) {
			n.node.accept(this, argu);
			System.out.print(':');
		}
	}

	/**
	 * f0 -> "MAIN"
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> "["
	 * f5 -> IntegerLiteral()
	 * f6 -> "]"
	 * f7 -> "["
	 * f8 -> IntegerLiteral()
	 * f9 -> "]"
	 * f10 -> StmtList()
	 * f11 -> "END"
	 * f12 -> ( Procedure() )*
	 * f13 -> <EOF>
	 */
	public void visit(Goal n, Method argu) {
		// Declaration
		System.out.println("\t.text");
		System.out.println("\t.globl\tmain");
		System.out.println("main:");
		// Build stack
		int stack = Integer.parseInt(n.f5.f0.tokenImage);
		int maxcall = Integer.parseInt(n.f8.f0.tokenImage);
		int actual = (Math.max(maxcall, 4) - 3) * 4;
		System.out.println("\tmove $fp, $sp");
		System.out.println(String.format("\tsubu $sp, $sp, %d", actual));
		System.out.println("\tsw $ra, -4($fp)");
		// Visit statements
		Method main = new Method(0, stack, maxcall);
		n.f10.accept(this, main);
		// Dealloc stack
		System.out.println("\tlw $ra, -4($fp)");
		System.out.println(String.format("\taddu $sp, $sp, %d", actual));
		System.out.println("\tj $ra");
		// Visit procedures
		n.f12.accept(this, null);
		// Tool procedures
		System.out.println("\n\t.text\n\t.globl _halloc\n_halloc:\n\tli $v0, 9\n\tsyscall\n\tj $ra");
		System.out.println(
				"\n\t.text\n\t.globl _print\n_print:\n\tli $v0, 1\n\tsyscall\n\tla $a0, newl\n\tli $v0, 4\n\tsyscall\n\tj $ra");
		System.out.println(
				"\n\t.text\n\t.globl _error\n_error:\n\tla $a0, str_er\n\tli $v0, 4\n\tsyscall\n\tli $v0, 10\n\tsyscall\n\tj $ra");
		System.out.println("\n\t.data\n\t.align\t0\nnewl:\t.asciiz\t\"\\n\"");
		System.out.println("\n\t.data\n\t.align\t0\nstr_er:\t.asciiz\t\" ERROR: abnormal termination\\n\"");
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> "["
	 * f5 -> IntegerLiteral()
	 * f6 -> "]"
	 * f7 -> "["
	 * f8 -> IntegerLiteral()
	 * f9 -> "]"
	 * f10 -> StmtList()
	 * f11 -> "END"
	 */
	public void visit(Procedure n, Method argu) {
		// Declaration
		String name = n.f0.f0.tokenImage;
		System.out.println("\n\t.text");
		System.out.println(String.format("\t.globl\t%s", name));
		System.out.println(String.format("%s:", name));
		// Build stack
		int param = Integer.parseInt(n.f2.f0.tokenImage);
		int stack = Integer.parseInt(n.f5.f0.tokenImage);
		int maxcall = Integer.parseInt(n.f8.f0.tokenImage);
		Method method = new Method(param, stack, maxcall);
		int actual = method.getActual();
		System.out.println("\tsw $fp, -8($sp)");
		System.out.println("\tmove $fp, $sp");
		System.out.println(String.format("\tsubu $sp, $sp, %d", actual));
		System.out.println("\tsw $ra, -4($fp)");
		// Visit statements
		n.f10.accept(this, method);
		// Dealloc stack
		System.out.println("\tlw $ra, -4($fp)");
		System.out.println(String.format("\tlw $fp, %d($sp)", actual - 8));
		System.out.println(String.format("\taddu $sp, $sp, %d", actual));
		System.out.println("\tj $ra");
	}

	/**
	 * f0 -> "NOOP"
	 */
	public void visit(NoOpStmt n, Method method) {
		System.out.println("\tnop");
	}

	/**
	 * f0 -> "ERROR"
	 */
	public void visit(ErrorStmt n, Method method) {
		System.out.println("\tjal _error");
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Reg()
	 * f2 -> Label()
	 */
	public void visit(CJumpStmt n, Method method) {
		System.out.print("\tbeqz $");
		n.f1.accept(this, method);
		System.out.print(' ');
		n.f2.accept(this, method);
		System.out.println();
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public void visit(JumpStmt n, Method method) {
		System.out.print("\tb ");
		n.f1.accept(this, method);
		System.out.println();
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Reg()
	 * f2 -> IntegerLiteral()
	 * f3 -> Reg()
	 */
	public void visit(HStoreStmt n, Method method) {
		System.out.print("\tsw $");
		n.f3.accept(this, method);
		System.out.print(", ");
		n.f2.accept(this, method);
		System.out.print(n.f2.f0.tokenImage);
		System.out.print("($");
		n.f1.accept(this, method);
		System.out.println(')');
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Reg()
	 * f2 -> Reg()
	 * f3 -> IntegerLiteral()
	 */
	public void visit(HLoadStmt n, Method method) {
		System.out.print("\tlw $");
		n.f1.accept(this, method);
		System.out.print(' ');
		n.f3.accept(this, method);
		System.out.print(n.f3.f0.tokenImage);
		System.out.print("($");
		n.f2.accept(this, method);
		System.out.println(')');
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Reg()
	 * f2 -> Exp()
	 */
	public void visit(MoveStmt n, Method method) {
		if (n.f2.f0.which == 2) {
			SimpleExp se = (SimpleExp) n.f2.f0.choice;
			switch (se.f0.which) {
			case 0:
				System.out.print("\tmove $");
				n.f1.accept(this, method);
				System.out.print(" $");
				se.accept(this, method);
				System.out.println();
				break;
			case 1:
				System.out.print("\tli $");
				n.f1.accept(this, method);
				System.out.println(String.format(" %s", ((IntegerLiteral) se.f0.choice).f0.tokenImage));
				break;
			case 2:
				System.out.print("\tla $");
				n.f1.accept(this, method);
				System.out.print(' ');
				se.accept(this, method);
				System.out.println();
				break;
			}
			return;
		}
		arg = n.f1;
		n.f2.accept(this, method);
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public void visit(PrintStmt n, Method method) {
		System.out.print("\tmove $a0 $");
		n.f1.accept(this, method);
		System.out.println();
		System.out.println("\tjal _print");
	}

	/**
	 * f0 -> "ALOAD"
	 * f1 -> Reg()
	 * f2 -> SpilledArg()
	 */
	public void visit(ALoadStmt n, Method method) {
		System.out.print("\tlw $");
		n.f1.accept(this, method);
		System.out.print(", ");
		n.f2.accept(this, method);
		System.out.println();
	}

	/**
	 * f0 -> "ASTORE"
	 * f1 -> SpilledArg()
	 * f2 -> Reg()
	 */
	public void visit(AStoreStmt n, Method method) {
		System.out.print("\tsw $");
		n.f2.accept(this, method);
		System.out.print(", ");
		n.f1.accept(this, method);
		System.out.println();
	}

	/**
	 * f0 -> "PASSARG"
	 * f1 -> IntegerLiteral()
	 * f2 -> Reg()
	 */
	public void visit(PassArgStmt n, Method method) {
		System.out.print("\tsw $");
		n.f2.accept(this, method);
		System.out.println(String.format(", %d($sp)", (Integer.parseInt(n.f1.f0.tokenImage) - 1) * 4));
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 */
	public void visit(CallStmt n, Method method) {
		System.out.print("\tjalr $");
		n.f1.accept(this, method);
		System.out.println();
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> SimpleExp()
	 */
	public void visit(HAllocate n, Method method) {
		switch (n.f1.f0.which) {
		case 0:
			System.out.print("\tmove $a0 $");
			n.f1.accept(this, method);
			System.out.println();
			break;
		case 1:
			System.out.println(
					String.format("\tli $a0 %s", ((IntegerLiteral) n.f1.f0.choice).f0.tokenImage));
			break;
		default:
			// Control should never reach here.
		}
		System.out.println("\tjal _halloc");
		System.out.print("\tmove $");
		arg.accept(this, method);
		System.out.println(" $v0");
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Reg()
	 * f2 -> SimpleExp()
	 */
	public void visit(BinOp n, Method method) {
		switch (n.f0.f0.which) {
		case 0:
			System.out.print("\tslt $");
			break;
		case 1:
			System.out.print("\tadd $");
			break;
		case 2:
			System.out.print("\tsub $");
			break;
		case 3:
			System.out.println("\tmul $");
			break;
		}
		arg.accept(this, method);
		System.out.print(", $");
		n.f1.accept(this, method);
		switch (n.f2.f0.which) {
		case 0:
			System.out.print(", $");
			n.f2.accept(this, method);
			break;
		case 1:
			System.out.print(String.format(", %s", ((IntegerLiteral) n.f2.f0.choice).f0.tokenImage));
			break;
		default:
			// Control should never reach here.
		}
		System.out.println();
	}

	/**
	 * f0 -> "a0"
	 *       | "a1"
	 *       | "a2"
	 *       | "a3"
	 *       | "t0"
	 *       | "t1"
	 *       | "t2"
	 *       | "t3"
	 *       | "t4"
	 *       | "t5"
	 *       | "t6"
	 *       | "t7"
	 *       | "s0"
	 *       | "s1"
	 *       | "s2"
	 *       | "s3"
	 *       | "s4"
	 *       | "s5"
	 *       | "s6"
	 *       | "s7"
	 *       | "t8"
	 *       | "t9"
	 *       | "v0"
	 *       | "v1"
	 */
	public void visit(Reg n, Method method) {
		System.out.print(n.f0.choice);
	}

	/**
	 * f0 -> "SPILLEDARG"
	 * f1 -> IntegerLiteral()
	 */
	public void visit(SpilledArg n, Method method) {
		System.out.print(method.getSpilledReg(Integer.parseInt(n.f1.f0.tokenImage)));
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public void visit(Label n, Method method) {
		System.out.print(n.f0.tokenImage);
	}

}
