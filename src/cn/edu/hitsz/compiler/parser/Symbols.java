package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;

/**
 * @description: Symbols
 * @date: 2022/9/30
 * @author: ZLX
 * version: 1.0
 */
class Symbols{
    Token token;
    NonTerminal nonTerminal;

    private Symbols(Token token, NonTerminal nonTerminal){
        this.token = token;
        this.nonTerminal = nonTerminal;
    }

    public Symbols(Token token){
        new Symbols(token, null);
    }

    public Symbols(NonTerminal nonTerminal){
        new Symbols(null, nonTerminal);
    }

    public boolean isToken(){
        return this.token != null;
    }

    public boolean isNonterminal(){
        return this.nonTerminal != null;
    }
}

