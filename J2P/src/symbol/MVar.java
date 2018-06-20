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

	/**
	 * 变量id：对于类的域，为vtable中的索引（索引0为dtable）；对于方法的局部变量和参数，为TEMP号。
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
