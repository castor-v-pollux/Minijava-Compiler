package piglet;

import symbol.MType;

/**
 * piglet�����װ�࣬�̳���{@link PCode}����ʾһ��piglet���ʽ��
 *
 * @author castor_v_pollux
 */
public abstract class PExp extends PCode {

	/**
	 * ���ʽ���ͣ��ڶԸñ��ʽ���з�������ʱ��Ҫ�õ�������Ϣ
	 */
	private MType type;

	public PExp setType(MType type) {
		this.type = type;
		return this;
	}

	public MType getType() {
		return type;
	}

	/**
	 * <p>����piglet���ʽ���飬���ʽ�����ɺ󲻼ӿո񡢻��еȿհ׷����հ׷���ʹ�ñ��ʽ��������ά����
	 * <p>�����ʽ����Ϊ���У����ر��ʽ���ȣ������ʽ����Ϊ���У�����{@link PCode#MULTILINE}��
	 */
	@Override
	public abstract int print(int depth);

}
