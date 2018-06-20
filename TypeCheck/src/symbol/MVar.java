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

	public MVar(String name, int row, int column, MType type) {
		super(name, row, column);
		this.type = type;
	}

	public MType getType() {
		return type;
	}

}
