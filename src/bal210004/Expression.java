// Starter code for Project 1

// Change this to your NetId
package bal210004;

import java.lang.reflect.Array;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Class to store a node of expression tree
 * For each internal node, element contains a binary operator
 * List of operators: +|*|-|/|%|^
 * Other tokens: (|)
 * Each leaf node contains an operand (long integer)
 */

public class Expression {
    public enum TokenType {  // NIL is a special token that can be used to mark bottom of stack
        PLUS, TIMES, MINUS, DIV, MOD, POWER, OPEN, CLOSE, NIL, NUMBER
    }

    public static class Token {
        TokenType token;
        int priority; // for precedence of operator
        Long number;  // used to store number of token = NUMBER
        String string;

        Token(TokenType op, int pri, String tok) {
            token = op;
            priority = pri;
            number = null;
            string = tok;
        }

        // Constructor for number.  To be called when other options have been exhausted.
        Token(String tok) {
            token = TokenType.NUMBER;
            number = Long.parseLong(tok);
            string = tok;
        }

        boolean isOperand() {
            return token == TokenType.NUMBER;
        }

        public long getValue() {
            return isOperand() ? number : 0;
        }

        public String toString() {
            return string;
        }
    }

    Token element;
    Expression left, right;

    // Create token corresponding to a string
    // tok is "+" | "*" | "-" | "/" | "%" | "^" | "(" | ")"| NUMBER
    // NUMBER is either "0" or "[-]?[1-9][0-9]*
    static Token getToken(String tok) {  // To do
        Token result;
        switch (tok) {
            case "+":
                result = new Token(TokenType.PLUS, 1, tok);  // modify if priority of "+" is not 1
                break;
            case "-":
                result = new Token(TokenType.MINUS, 1, tok);
                break;
            case "*":
                result=new Token(TokenType.TIMES, 2, tok);
                break;
            case "/":
                result=new Token(TokenType.DIV, 2, tok);
                break;
            case "%":
                result=new Token(TokenType.MOD, 2, tok);
                break;
            case "^":
                result=new Token(TokenType.POWER, 3, tok);
                break;
            case "(":
                result=new Token(TokenType.OPEN,4,tok);
                break;
            case ")":
                result=new Token(TokenType.CLOSE,4,tok);
                break;
            // Complete rest of this method
            default:
                result = new Token(tok);
                break;
        }
        return result;
    }

    private Expression() {
        element = null;
    }

    private Expression(Token oper, Expression left, Expression right) {
        this.element = oper;
        this.left = left;
        this.right = right;
    }

    private Expression(Token num) {
        this.element = num;
        this.left = null;
        this.right = null;
    }

    // Given a list of tokens corresponding to an infix expression,
    // return the expression tree corresponding to it.
    public static Expression infixToExpression(List<Token> exp) {  // To do
        ArrayDeque<Expression> outputStack=new ArrayDeque<>();
        ArrayDeque<Token> operatorStack=new ArrayDeque<>();
        Iterator<Token> tmpIter=exp.listIterator();
        Token tmp;
        Expression op1;
        Expression op2;
        while(tmpIter.hasNext()){
            tmp=tmpIter.next();
            if(tmp.isOperand()){
                outputStack.push(new Expression(tmp));
            } else {
                if(tmp.token==TokenType.CLOSE){ //pop until open is reached
                    while(operatorStack.peek()!=null&& operatorStack.peek().token!=TokenType.OPEN){
                        op2=outputStack.pop();
                        op1=outputStack.pop();
                        outputStack.push(new Expression(operatorStack.pop(),op1,op2));
                    }
                    operatorStack.pop();//discard the open parentheses
                } else { //normal operator, pop until tmp is greater than the top of the stack, then push tmp
                    while(operatorStack.peek()!=null&&operatorStack.peek().priority>=tmp.priority&&operatorStack.peek().token!=TokenType.OPEN){
                        op2=outputStack.pop();
                        op1=outputStack.pop();
                        outputStack.push(new Expression(operatorStack.pop(),op1,op2));
                    }
                    operatorStack.push(tmp);
                }
            }
        }
        //no more input, pop operators until operatorStack is empty
        while(operatorStack.peek()!=null){
            op2=outputStack.pop();
            op1=outputStack.pop();
            outputStack.push(new Expression(operatorStack.pop(),op1,op2));
        }
        return outputStack.pop();
    }

    // Given a list of tokens corresponding to an infix expression,
    // return its equivalent postfix expression as a list of tokens.
    public static List<Token> infixToPostfix(List<Token> exp) {  // To do
        ArrayDeque<Token> stack = new ArrayDeque<>();
        List<Token> retExpression=new LinkedList<>();
        Iterator<Token> tmpIter=exp.listIterator();
        Token tmp;
        while(tmpIter.hasNext()){
            tmp=tmpIter.next();
            if(tmp.isOperand()){
                retExpression.add(tmp);
            } else {
                if(tmp.token==TokenType.CLOSE){
                    while(stack.peek()!=null&&stack.peek().token!=TokenType.OPEN) // pop all operators until open parentheses is found
                        retExpression.add(stack.pop());
                    stack.pop();//discard the open parentheses
                } else { // normal operator, evaluate priority of the token at end of the stack, then push
                    while (stack.peek() != null && stack.peek().priority >= tmp.priority && stack.peek().token!=TokenType.OPEN) {
                        retExpression.add(stack.pop());
                    }
                    stack.push(tmp);
                }
            }
        }
        while(stack.peek()!=null){
            retExpression.add(stack.pop());
        }
        return retExpression;
    }

    // Given a postfix expression, evaluate it and return its value.
    public static long evaluatePostfix(List<Token> exp) {  // To do
        ArrayDeque<Token> stack = new ArrayDeque<>();
        ListIterator<Token> tmpIter=exp.listIterator();
        Token tmp;
        long op1;
        long op2;
        while(tmpIter.hasNext()){
            tmp=tmpIter.next();
            if(tmp.isOperand()){
                stack.push(tmp);
            } else {
                op2 = stack.pop().getValue();
                op1 = stack.pop().getValue();
                switch(tmp.token){
                    case PLUS:
                        stack.push(getToken((op1+op2)+""));
                        break;
                    case MINUS:
                        stack.push(getToken((op1-op2)+""));
                        break;
                    case TIMES:
                        stack.push(getToken((op1*op2)+""));
                        break;
                    case DIV:
                        stack.push(getToken((op1/op2)+""));
                        break;
                    case POWER:
                        stack.push(getToken(((long)Math.pow(op1,op2))+""));
                        break;
                    case MOD:
                        stack.push(getToken((op1%op2)+""));
                        break;
                }
            }
        }
        return stack.pop().getValue();
    }

    // Given an expression tree, evaluate it and return its value.
    public static long evaluateExpression(Expression tree) {// To do
        if(tree.element.isOperand()) {
            return tree.element.getValue();
        } else {
            long op1 = evaluateExpression(tree.left);
            long op2 = evaluateExpression(tree.right);
            switch (tree.element.token) {
                case PLUS:
                    return op1 + op2;
                case MINUS:
                    return op1 - op2;
                case TIMES:
                    return op1 * op2;
                case DIV:
                    return op1 / op2;
                case MOD:
                    return op1 % op2;
                case POWER:
                    return (long) Math.pow(op1, op2);
                default://this statement should not be reached, if reached, error occurred
                    return 0;
            }
        }
    }

    // sample main program for testing
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;

        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }

        int count = 0;
        while (in.hasNext()) {
            String s = in.nextLine();
            List<Token> infix = new LinkedList<>();
            Scanner sscan = new Scanner(s);
            int len = 0;
            while (sscan.hasNext()) {
                infix.add(getToken(sscan.next()));
                len++;
            }
            if (len > 0) {
                count++;
                System.out.println("Expression number: " + count);
                System.out.println("Infix expression: " + infix);
                Expression exp = infixToExpression(infix);
                List<Token> post = infixToPostfix(infix);
                System.out.println("Postfix expression: " + post);
                long pval = evaluatePostfix(post);
                long eval = evaluateExpression(exp);
                System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
            }
        }
    }
}
