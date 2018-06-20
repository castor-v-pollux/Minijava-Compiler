package piglet;

import java.util.ArrayList;

/**
 * piglet代码包装类，继承自{@link PCode}，表示一个完整的piglet源程序。
 *
 * @author castor_v_pollux
 */
public class PGoal extends PCode {

	private PStmtList pStmtList;

	private ArrayList<PProcedure> pProcedures = new ArrayList<>();

	public PGoal(PStmtList pStmtList) {
		this.pStmtList = pStmtList;
	}

	public void addAll(ArrayList<PProcedure> procedureList) {
		pProcedures.addAll(procedureList);
	}

	@Override
	public int print(int depth) {
		// 默认depth=0，略去MAIN，END等的缩进处理
		System.out.println("MAIN");
		pStmtList.print(TAB_WIDTH);
		System.out.println("END");
		for (PProcedure procedure : pProcedures)
			procedure.print(0);
		return MULTILINE;
	}

}
