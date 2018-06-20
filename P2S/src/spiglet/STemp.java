package spiglet;

/**
 * SPiglet��TEMP��װ��
 *
 * @author castor_v_pollux
 */
public class STemp extends SCode {

	private static int TempCnt;

	/**
	 * ��ʼ��ȫ�ֵ�ǰ�Ĵ�����
	 * @param total ��ǰ���üĴ���
	 */
	public static void init(int total) {
		TempCnt = total;
	}

	public static STemp newTemp() {
		return new STemp(++TempCnt);
	}

	private int id;

	public STemp(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("TEMP %d", id);
	}

}
