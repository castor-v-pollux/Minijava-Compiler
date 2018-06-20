package piglet;

/**
 * <p>piglet�����װ�࣬�̳���{@link PStmt}����ʾһ��Label����ǩ��
 * <p>ά��һ��ȫ�ּ��������������Label��id��
 *
 * @author castor_v_pollux
 */
public class PLabel extends PStmt {

	private static int LabelCnt = 0;

	private int id;

	private PLabel(int id) {
		this.id = id;
	}

	public static PLabel newLabel() {
		return new PLabel(LabelCnt++);
	}

	@Override
	public int print(int depth) {
		String s = String.format("L%d", id);
		System.out.print(s);
		return s.length();
	}

}
