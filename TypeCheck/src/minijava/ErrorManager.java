package minijava;

/**
 * ���ͼ�����Ĵ����ࡣ���е����ͼ����󶼻Ἧ�е���������ڸ����н��ж��ƻ��Ĵ���
 *
 * @author castor_v_pollux
 */
public class ErrorManager {

	/**
	 * ������Ŀǰ�Ĵ���ʽΪ��ӡ������Ϣ���˳�����
	 * @param error ���ͼ��Ĵ���
	 */
	public static void error(TypeCheckError error) {
		System.out.println(String.format("Row %d, Col %d: %s", error.getRow(), error.getColumn(), error.getMessage()));
		System.exit(0);
	}

}
