package s2k;

import java.io.InputStream;

import parser.ParseException;
import parser.SpigletParser;
import symbol.SymbolTable;
import syntaxtree.Goal;
import visitor.BuildFlowGraphVisitor;
import visitor.BuildSymbolTableVisitor;
import visitor.TranslateVisitor;

/**
 * <p>该类为第四次作业的主类，负责将SPiglet代码翻译成Kanga代码。
 * <p>思路为：从Spiglet到Kanga主要是处理寄存器分配问题，本程序分为四步，使用了三个Visitor:
 * <ul>
 * <li>第一步使用{@link BuildSymbolTableVisitor}对每个方法中的语句进行编号，并将每个标号与语句编号相联系。
 * <li>第二步使用{@link BuildFlowGraphVisitor}将每个方法中语句建立为一个流图，供活性分析使用。
 * <li>第三步使用{@link SymbolTable#LinearScan()}在每个方法内部进行活性分析和寄存器分配。
 * <li>第四步使用{@link TranslateVisitor}利用第三步得到的寄存器分配结果进行语句的翻译。
 * </ul>
 *
 * @author castor_v_pollux
 */
public class Main {

	public static void main(String[] args) {
		InputStream in = System.in;
		// try {
		// in = new FileInputStream(args[0]);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// return;
		// }
		Goal goal;
		try {
			goal = new SpigletParser(in).Goal();
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		goal.accept(new BuildSymbolTableVisitor(), null);
		goal.accept(new BuildFlowGraphVisitor(), null);
		SymbolTable.LinearScan();
		goal.accept(new TranslateVisitor(), null);
	}

}
