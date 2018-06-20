package j2p;

import java.io.InputStream;

import parser.MiniJavaParser;
import parser.ParseException;
import piglet.PAllocate;
import piglet.PBinOp;
import piglet.PCJumpStmt;
import piglet.PCall;
import piglet.PCode;
import piglet.PErrorStmt;
import piglet.PExp;
import piglet.PGoal;
import piglet.PIdentifier;
import piglet.PInteger;
import piglet.PJumpStmt;
import piglet.PLabel;
import piglet.PLoadStmt;
import piglet.PMoveStmt;
import piglet.PNoOpStmt;
import piglet.PPrintStmt;
import piglet.PProcedure;
import piglet.PStmt;
import piglet.PStmtExp;
import piglet.PStmtList;
import piglet.PStoreStmt;
import piglet.PTemp;
import symbol.MClass;
import symbol.MIdentifier;
import symbol.MMethod;
import symbol.MType;
import symbol.MVar;
import symbol.SymbolTable;
import syntaxtree.Node;
import visitor.ClassVisitor;
import visitor.MemberVisitor;
import visitor.TranslateVisitor;

/**
 * <p>����Ϊ�ڶ�����ҵJ2P�����ࡣ
 * <p>���¶���������ļܹ���������̺�Piglet���������淶����˵����
 * 
 * <p>��������Ϯ�˵�һ����ҵ�ж�MiniJava����ϵͳ�ķ�װ����MiniJava�еı�ʶ������Ϊ�����ࣺ
 * <ul>
 * <li>{@link MIdentifier}    ��ʶ���ĸ��࣬��¼���ơ���������
 * <li>|---{@link MClass}     ��ʾһ���࣬ά���䷽���������б��
 * <li>|---{@link MMethod}    ��ʾһ��������ά��������������б��
 * <li>|---{@link MVar}       ��ʾһ��������ά��������
 * </ul>
 * <p>���⽫MiniJava�е����ͳ���Ϊ���½ӿڣ�
 * <ul>
 * <li>{@link MType}                 ���͵ĸ��ӿڣ���������{@link MType#getName()}��{@link MType#canConvertTo(MType)}
 * <li>|---{@link MClass}            ����Ϊ�������͵ĳ����ߣ��̳иýӿ�
 * <li>|---{@link MType#IntType}     �ӿڵĳ���ʵ�֣���ʾ{@code int}����
 * <li>|---{@link MType#BooleanType} �ӿڵĳ���ʵ�֣���ʾ{@code boolean}����
 * <li>|---{@link MType#ArrayType}   �ӿڵĳ���ʵ�֣���ʾ{@code int[]}����
 * <li>|---{@link MType#OtherType}   �ӿڵĳ���ʵ�֣���ʾ{@code String[]}����
 * </ul>
 * <p>�͵�һ����ҵ��ͬ�������ķ�װ����ʮ�����ŵؽ�������ű������ַ����Ĳ���ֻ�ڹ����﷨���ĵײ���У�����Ժܵ͡�
 * 
 * <p>���⣬Ϊ�����ɵ�Piglet�����кܸߵĿɶ��ԣ����¶�����һ�׹���Piglet���������淶��
 * <ul>
 * <li>����Goal��StmtExp�е�StmtList��������һ��Tab��ȣ���StmtList��ĳ���ǰ��Label����Label���������ǰ���롣
 * <li>һ��Piglet���ʽ����Խ���У�ÿһ�б�������һ�п�ͷ���룬���������ʽ�ڴ�����Ӧ����һ�����������ڡ�
 * <li>һ��Piglet����ж������ʱ����ȫΪ���в�������д��һ���ڣ��������ÿһ�����еĲ����������һ������Ӧ���һ��������ͷ���룬������֤�����Ĳ����б���һ�����������ڡ�
 * </ul>
 *
 * <p>���������ɵ�Piglet����ȫ����ѭ�ù淶��Ϊ�ˣ���Piglet������������·�װ��
 * <ul>
 * <li>{@link PCode}                Piglet����ĳ�����࣬������Piglet�����ӡ�Ļ����淶
 * <li>|---{@link PExp}             Piglet���ʽ�ĳ�����࣬������Piglet���ʽ��ӡ�Ļ����淶
 * <li>|---|---{@link PAllocate}    HAllocate
 * <li>|---|---{@link PBinOp}       BinOp
 * <li>|---|---{@link PCall}        Call
 * <li>|---|---{@link PIdentifier}  ��ʶ����������������������ַ
 * <li>|---|---{@link PInteger}     ����
 * <li>|---|---{@link PStmtExp}     StmtExp
 * <li>|---|---{@link PTemp}        Temp
 * <li>|---{@link PGoal}            Goal
 * <li>|---{@link PProcedure}       Procedure
 * <li>|---{@link PStmt}            Piglet���ĳ�����࣬������Piglet����ӡ�Ļ����淶
 * <li>|---|---{@link PCJumpStmt}   CJump
 * <li>|---|---{@link PErrorStmt}   Error
 * <li>|---|---{@link PJumpStmt}    Jump
 * <li>|---|---{@link PLabel}       Label
 * <li>|---|---{@link PLoadStmt}    HLoad
 * <li>|---|---{@link PMoveStmt}    Move
 * <li>|---|---{@link PNoOpStmt}    Noop
 * <li>|---|---{@link PPrintStmt}   Print
 * <li>|---|---{@link PStoreStmt}   HStore
 * <li>|---{@link PStmtList}        StmtList
 * </ul>
 * <p>ÿһ��Piglet�����װ���ж���print����������ָ������λ�ô�ӡ�ô���Σ��ھ�������и��Ը��ݹ淶ʵ��print����������ʵ��Piglet���������������
 * 
 * <p>��������Ҫ��Ϊ�岽��ʹ��������Visitor���б�����ǰ�������һ����ҵ��ͬ���������£�
 * <ul>
 * <li>1.ʹ��{@link ClassVisitor}��������������������֣�����Щ�������ű���
 * <li>2.ʹ��{@link MemberVisitor}������������ĳ�Ա�����ͷ������Լ����������ľֲ������������Ǽ��뵽���ű���
 * <li>3.ʹ��{@link SymbolTable#checkClassExtension()}���ڷ��ű��ڲ������ļ̳й�ϵ����ʹ����������ȷ���̳й�ϵ�޻��󣬸�������˳��������Ա�����ͷ����ļ̳�
 * <li>4.ʹ��{@link TranslateVisitor}���������������е�����б����䷭���{@link PCode}�������
 * <li>5.�����յõ���{@code PCode}������д�ӡ
 * </ul>
 * 
 * <p>�ڴ���ķ�����������ע��㣺
 * <ul>
 * <li>����ԭMiniJava�����͵Ĵ���
 * 		<ul>
 * 		<li>int,boolean������Piglet�е�����������true����1��false����0
 * 		<li>int[]��Piglet��ʹ��һ����ַ����ʾ���õ�ַָ��һ�鱻����Ŀռ䣬��ͷ���ֽ������鳤��n������4n�ֽ��������ʵ������
 * 		<li>��ʵ����Piglet��ʹ��һ����ַ����ʾ���õ�ַָ��һ�鱻����Ŀռ䣬��ͷ���ֽ���һ����ַ��ָ���ʵ������ķ��������������Ǹ�ʵ�������Ա�
 * 		</ul>
 * <li>���ڷ�����̬�����Ը��ǣ�
 * 		<ul>
 * 		<li>����������ȫ�̳и���ķ�������������෽�����ǰ�沿���븸�෽����һ�£��������صķ������滻��Ӧ�ķ�����ַ���ɣ����෽����ĺ��沿�������������ӵķ�����
 * 		<li>�����������Խ������Ǹ������ԣ��ڵ��ø��෽��ʱ��Ȼ�����õ��������ԣ���˶�������͸�����������ԣ����ܽ����滻�����Ƕ������������Զ����ٶ���Ŀռ䡣
 * 		</ul>
 * <li>���ڱ�����ʼ����
 * 		<ul>
 * 		<li>��ĳ�Ա������ʵ��������ʱȫ����ʼ��Ϊ0
 * 		<li>�����ľֲ������ڽ���÷���ʱȫ����ʼ��Ϊ0
 * 		<li>����������ڷ��������ʱȫ����ʼ��Ϊ0
 * 		</ul>
 * <li>���ھ������ķ��룺
 * 		<ul>
 * 		<li>��ʹ�������ͱ���������������飩ʱ����Ҫ�ȼ��ǿ�
 * 		<li>�ڶ���������±����ʱ����Ҫ��������Ƿ�Խ��
 * 		<li>���½�����ʱ�����鳤�ȱ���Ǹ�
 * 		<li>����&&���ʽ�Ĵ������������Ϊ0ʱ���������Ҳ���������·�룩
 * 		</ul>
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
		Node goal;
		try {
			goal = new MiniJavaParser(in).Goal();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return;
		}
		goal.accept(new ClassVisitor());
		goal.accept(new MemberVisitor(), null);
		SymbolTable.performClassExtension();
		PCode code = (PCode) goal.accept(new TranslateVisitor(), null);
		code.print(0);
	}

}
