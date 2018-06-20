package symbol;

/**
 * 该类表示Minijava中的一个变量，可能是类的域或者方法的局部变量。继承自{@link MIdentifier}。
 * 
 * @author castor_v_pollux
 */
public class MVar extends MIdentifier {

	/**
	 * 该变量的类型
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
