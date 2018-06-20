package kanga;

/**
 * <p>�����ʾһ��Kanga��������¼���������������ж�ջ֡�ķ��䡣һ��ջ֡����ͼ��ʾ��
 * <ul>
 * <li>Ret addr (4 bytes)
 * <li>Previous frame pointer (4 bytes)
 * <li>Stack used (stack-ReLU(param-4) words)
 * <li>Space for parameters passed to next frame pointer (ReLU(maxcall-4) words)
 * </ul>
 * <p>�������£�����4���ֽڵķ��ص�ַ��Ȼ������һ��ջ֡�Ŀ�ʼ��ַ4�ֽڣ�
 * <p>Ȼ���Ǳ�����ʹ�õ�ջ��Ԫ��ע�⵽Kanga����ͷд�ĵڶ��������а����˱����̳����ĸ�������ջ��Ԫ�����Ҫ�۳���
 * <p>����Ǳ����̵��ñ�Ĺ���ʱ���������ĸ�����ʱ�õ���ջ��Ԫ�� * 
 *
 * @author castor_v_pollux
 */
public class Method {

	private int extraParam, stack, maxcall, pos;

	public Method(int param, int stack, int maxcall) {
		this.extraParam = Math.max(param - 4, 0);
		this.stack = stack;
		this.maxcall = maxcall;
		pos = Math.max(maxcall - 4, 0);
	}

	/**
	 * ��ȡ������ʵ�ʵ�ջ֡��С
	 * @return ʵ��ջ֡��С
	 */
	public int getActual() {
		return (2 + stack - extraParam + pos) * 4;
	}

	/**
	 * ����SpilledReg�ķ���תΪջ��ַ�ķ��ʣ�����Ǳ�����Ĳ�����ȥ��һ��ջ֡�ң�����Ǳ������ջ��Ԫ�����м�һ���ҡ�
	 * @param id ջ��Ԫid
	 * @return ջ��ַ
	 */
	public String getSpilledReg(int id) {
		if (id < extraParam)
			return String.format("%d($fp)", id * 4);
		return String.format("%d($sp)", (id - extraParam + pos) * 4);
	}
}
