package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PExp}����ʾһ��piglet�������ʽ��
 *
 * @author castor_v_pollux
 */
public class PInteger extends PExp {

	private int value;

	public PInteger(int value) {
		this.value = value;
	}

	@Override
	public int print(int depth) {
		String s = String.valueOf(value);
		System.out.print(s);
		return s.length();
	}

}
