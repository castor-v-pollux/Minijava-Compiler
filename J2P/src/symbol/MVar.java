package symbol;

/**
 * �����ʾMinijava�е�һ���������������������߷����ľֲ��������̳���{@link MIdentifier}��
 * 
 * @author castor_v_pollux
 */
public class MVar extends MIdentifier {

	/**
	 * �ñ���������
	 */
	private MType type;

	/**
	 * ����id�����������Ϊvtable�е�����������0Ϊdtable�������ڷ����ľֲ������Ͳ�����ΪTEMP�š�
	 */
	private int id;

	public MVar(String name, MType type) {
		super(name);
		this.type = type;
	}

	public MType getType() {
		return type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
