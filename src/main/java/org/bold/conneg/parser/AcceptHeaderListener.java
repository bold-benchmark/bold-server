// Generated from AcceptHeader.g4 by ANTLR 4.12.0

package org.bold.conneg.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AcceptHeaderParser}.
 */
public interface AcceptHeaderListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AcceptHeaderParser#accept}.
	 * @param ctx the parse tree
	 */
	void enterAccept(AcceptHeaderParser.AcceptContext ctx);
	/**
	 * Exit a parse tree produced by {@link AcceptHeaderParser#accept}.
	 * @param ctx the parse tree
	 */
	void exitAccept(AcceptHeaderParser.AcceptContext ctx);
	/**
	 * Enter a parse tree produced by {@link AcceptHeaderParser#mediaRange}.
	 * @param ctx the parse tree
	 */
	void enterMediaRange(AcceptHeaderParser.MediaRangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AcceptHeaderParser#mediaRange}.
	 * @param ctx the parse tree
	 */
	void exitMediaRange(AcceptHeaderParser.MediaRangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AcceptHeaderParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(AcceptHeaderParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AcceptHeaderParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(AcceptHeaderParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AcceptHeaderParser#subtype}.
	 * @param ctx the parse tree
	 */
	void enterSubtype(AcceptHeaderParser.SubtypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AcceptHeaderParser#subtype}.
	 * @param ctx the parse tree
	 */
	void exitSubtype(AcceptHeaderParser.SubtypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AcceptHeaderParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(AcceptHeaderParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link AcceptHeaderParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(AcceptHeaderParser.ParameterContext ctx);
}