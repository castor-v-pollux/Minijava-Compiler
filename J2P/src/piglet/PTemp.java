package piglet;

/**
 * <p>piglet�����װ�࣬�̳���{@link PExp}����ʾһ��piglet��ʱ���浥ԪTEMP��
 * <p>ά��һ��ȫ�ּ��������������Temp��id��
 * <p>���ǵ�����������ʹ��TEMP 0~19���ʽ���һ������أ�����0~19��Tempֱ�Ӵӻ�����л�ȡ��
 *
 * @author castor_v_pollux
 */
public class PTemp extends PExp {

	/**
	 * PTemp�����
	 */
	private static PTemp[] cache = new PTemp[20];

	static {
		for (int i = 0; i < 20; i++)
			cache[i] = new PTemp(i);
	}

	private static int TempCnt = 20;

	private int id;

	private PTemp(int id) {
		this.id = id;
	}

	/**
	 * ��ȡһ���µ�Temp id
	 * @return һ�����õĴ��浥Ԫ��
	 */
	public static int newTempId() {
		return TempCnt++;
	}

	/**
	 * ��ȡһ���µ�Temp
	 * @return һ�����õĴ��浥Ԫ
	 */
	public static PTemp newTemp() {
		return new PTemp(TempCnt++);
	}

	/**
	 * ��id��ȡһ��PTempʵ��
	 * @param id PTemp���
	 * @return PTempʵ��
	 */
	public static PTemp valueOf(int id) {
		if (id < 20)
			return cache[id];
		else
			return new PTemp(id);
	}

	@Override
	public int print(int depth) {
		String s = String.format("TEMP %d", id);
		System.out.print(s);
		return s.length();
	}

}
