package piglet;

/**
 * <p>�ó�����Ϊpiglet�����Ļ��࣬�涨��piglet�����װ��Ļ����ӿڹ淶��
 *
 * @author castor_v_pollux
 */
public abstract class PCode {

	/**
	 * һ��������������ȣ��������4��������λ����label��ռ��4λ�����ռ䣩
	 */
	protected static final int TAB_WIDTH = 6;

	/**
	 * ��ǳ�����������ʾ�ô��������
	 */
	protected static final int MULTILINE = -1;

	/**
	 * <p>���ڵ��д���Σ�ֱ�Ӵ�ӡ�ô��룻
	 * <p>���ڶ��д���Σ��ڵ�ǰ�������depthλ��������ô���Ρ�
	 * @param depth ���ڶ��д��룬��ǰ�������������
	 * @return ���ڵ��д��룬����������ַ��������ڶ��д��룬����{@link PCode#MULTILINE}��
	 */
	public abstract int print(int depth);

	/**
	 * ���߷������ӵ�ǰλ����������depth���ո�
	 * @param depth �������
	 */
	protected void indent(int depth) {
		for (int i = 0; i < depth; i++)
			System.out.print(' ');
	}

	/**
	 * ���߷��������һ���ո�
	 */
	protected void space() {
		System.out.print(' ');
	}

}
