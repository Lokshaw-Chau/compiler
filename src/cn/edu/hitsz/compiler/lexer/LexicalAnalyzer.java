package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import javax.swing.plaf.IconUIResource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private String code = null;
    private List<Token> tokenList = new ArrayList<>();
    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        //throw new NotImplementedException();
        File src = new File(path);
        Long fileLengthLong = src.length();
        byte[] fileContent = new byte[fileLengthLong.intValue()];
        try {
            FileInputStream inputStream = new FileInputStream(src);
            inputStream.read(fileContent);
            inputStream.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        code = new String(fileContent);
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // 自动机实现的词法分析过程
        int begin = 0;
        int forward;
        int status;
        String letter = "abcdefghijklmnopqrstuvwxyz";
        String digit = "1234567890";
        while (begin < code.length()) {
            status = 0;
            forward = begin;
            int flag = 0;
            while (flag == 0) {
                switch (status) {
                    //start
                    case 0:
                        switch (code.charAt(forward)) {
                            case ' ':
                            case '\n':
                            case '\r':
                            case '\t':
                                flag = 1;
                                status = 0;
                                break;
                            case '*':
                                status = 18;
                                break;
                            case '=':
                                status = 21;
                                break;
                            case '"':
                                status = 24;
                                break;
                            case '(':
                                status = 26;
                                break;
                            case ')':
                                status = 27;
                                break;
                            case ';':
                                status = 28;
                                break;
                            case '+':
                                status = 29;
                                break;
                            case '-':
                                status = 30;
                                break;
                            case '/':
                                status = 31;
                                break;
                            case ',':
                                status = 32;
                                break;
                            default:
                                //letter
                                if (letter.contains(code.charAt(forward) + "")) {
                                    status = 14;
                                }
                                //digit
                                else if (digit.contains(code.charAt(forward) + "")) {
                                    status = 16;
                                } else {
                                    status = 0;
                                }
                                break;
                        }
                        break;
                    case 14:
                        if (letter.contains(code.charAt(forward) + "") || digit.contains(code.charAt(forward) + "")) {
                            status = 14;
                        } else {
                            status = 15;
                        }
                        break;
                    case 15:
                        if(code.substring(begin, forward - 1).equals("int")){
                            tokenList.add(Token.simple("int"));
                        }
                        else if(code.substring(begin, forward - 1).equals("return")){
                            tokenList.add(Token.simple("return"));
                        }
                        else{
                            tokenList.add(Token.normal("id", code.substring(begin, forward - 1)));
                            if(!symbolTable.has(code.substring(begin, forward - 1))){
                                symbolTable.add(code.substring(begin, forward - 1));
                            }
                        }
                        flag = 1;
                        forward = forward-2;
                        break;
                    case 16:
                        if (digit.contains(code.charAt(forward) + "")) {
                            status = 16;
                        } else {
                            status = 17;
                        }
                        break;
                    case 17:
                        tokenList.add(Token.normal("IntConst", code.substring(begin, forward - 1)));
                        forward = forward-2;
                        flag = 1;
                        break;
                    case 18:
                        switch (code.charAt(forward)) {
                            case '*':
                                status = 20;
                                break;
                            default:
                                status = 19;
                                break;
                        }
                        break;
                    case 19:
                        flag = 1;
                        tokenList.add(Token.simple("*"));
                        forward = forward-1;
                        break;
                    case 20:
                        flag = 1;
                        tokenList.add(Token.simple("**"));
                        forward = forward-1;
                    case 21:
                        switch (code.charAt(forward)) {
                            case '=':
                                status = 22;
                                break;
                            default:
                                status = 23;
                                break;
                        }
                        break;
                    case 22:
                        flag = 1;
                        tokenList.add(Token.simple("=="));
                        forward = forward-1;
                        break;
                    case 23:
                        flag = 1;
                        tokenList.add(Token.simple("="));
                        forward = forward-1;
                        break;
                    case 24:
                        if (code.charAt(forward) == '"') {
                            status = 25;
                        } else {
                            status = 24;
                        }
                        break;
                    case 25:
                        flag = 1;
                        tokenList.add(Token.normal("String", code.substring(begin, forward)));
                        forward = forward-1;
                        break;
                    case 26:
                        flag = 1;
                        tokenList.add(Token.simple("("));
                        forward = forward-1;
                        break;
                    case 27:
                        flag = 1;
                        tokenList.add(Token.simple(")"));
                        forward = forward-1;
                        break;
                    case 28:
                        flag = 1;
                        tokenList.add(Token.simple("Semicolon"));
                        forward = forward-1;
                        break;
                    case 29:
                        flag = 1;
                        tokenList.add(Token.simple("+"));
                        forward = forward-1;
                        break;
                    case 30:
                        flag = 1;
                        tokenList.add(Token.simple("-"));
                        forward = forward-1;
                        break;
                    case 31:
                        flag = 1;
                        tokenList.add(Token.simple("/"));
                        forward = forward-1;
                        break;
                    case 32:
                        flag = 1;
                        tokenList.add(Token.simple(","));
                        forward = forward-1;
                        break;
                    default:
                }
                forward++;
            }
            begin = forward;
        }
        tokenList.add(Token.eof());
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        //throw new NotImplementedException();
        return tokenList;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
