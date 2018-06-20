package piglet;

/**
 * piglet代码包装类，继承自{@link PExp}，表示BinOp表达式，有比较，加法，减法，乘法四种。
 *
 * @author castor_v_pollux
 */
public class PBinOp extends PExp {

	/**
	 * 枚举类，BinOp表达式的运算符
	 */
	public static enum PBinOpType {
		LT, PLUS, MINUS, TIMES
	}

	private PBinOpType op;

	private PExp pExp1, pExp2;

	public PBinOp(PBinOpType op, PExp pExp1, PExp pExp2) {
		this.op = op;
		this.pExp1 = pExp1;
		this.pExp2 = pExp2;
	}

	@Override
	public int print(int depth) {
		int l, l1, l2;
		System.out.print(op.name());
		l = op.name().length() + 1;
		space();
		l1 = pExp1.print(depth + l);
		if (l1 == MULTILINE) {
			// 若第一个运算数跨行，则应换行，让第二个运算数与第一个对齐
			System.out.println();
			indent(depth + l);
			pExp2.print(depth + l);
			return MULTILINE;
		} else {
			space();
			l += l1 + 1;
			l2 = pExp2.print(depth + l);
			if (l2 == MULTILINE)
				return MULTILINE;
			else
				return l + l2;
		}
	}

}
