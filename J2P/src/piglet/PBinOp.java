package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PExp}����ʾBinOp���ʽ���бȽϣ��ӷ����������˷����֡�
 *
 * @author castor_v_pollux
 */
public class PBinOp extends PExp {

	/**
	 * ö���࣬BinOp���ʽ�������
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
			// ����һ�����������У���Ӧ���У��õڶ������������һ������
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
