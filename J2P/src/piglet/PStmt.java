package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PCode}����ʾһ��piglet��䡣
 *
 * @author castor_v_pollux
 */
public abstract class PStmt extends PCode {

	/**
	 * <p>����piglet���飬������䱾���Ƿ�Ϊ���У������ɺ󶼽��л��У��ϲ�ֻ�������ǰ������������
	 * <p>�ط���{@link PCode#MULTILINE}��
	 */
	@Override
	public abstract int print(int depth);

}
