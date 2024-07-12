// Generated from AcceptHeader.g4 by ANTLR 4.12.0

package org.bold.conneg.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class AcceptHeaderParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, TOKEN=7, QVALUE=8, OWS=9;
	public static final int
		RULE_accept = 0, RULE_mediaRange = 1, RULE_type = 2, RULE_subtype = 3, 
		RULE_parameter = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"accept", "mediaRange", "type", "subtype", "parameter"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "'*/*'", "'/*'", "'/'", "';'", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "TOKEN", "QVALUE", "OWS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "AcceptHeader.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AcceptHeaderParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AcceptContext extends ParserRuleContext {
		public List<MediaRangeContext> mediaRange() {
			return getRuleContexts(MediaRangeContext.class);
		}
		public MediaRangeContext mediaRange(int i) {
			return getRuleContext(MediaRangeContext.class,i);
		}
		public AcceptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_accept; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).enterAccept(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).exitAccept(this);
		}
	}

	public final AcceptContext accept() throws RecognitionException {
		AcceptContext _localctx = new AcceptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_accept);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
			mediaRange();
			setState(15);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(11);
				match(T__0);
				setState(12);
				mediaRange();
				}
				}
				setState(17);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MediaRangeContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public SubtypeContext subtype() {
			return getRuleContext(SubtypeContext.class,0);
		}
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public MediaRangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mediaRange; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).enterMediaRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).exitMediaRange(this);
		}
	}

	public final MediaRangeContext mediaRange() throws RecognitionException {
		MediaRangeContext _localctx = new MediaRangeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_mediaRange);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(18);
				match(T__1);
				}
				break;
			case 2:
				{
				setState(19);
				type();
				setState(20);
				match(T__2);
				}
				break;
			case 3:
				{
				setState(22);
				type();
				setState(23);
				match(T__3);
				setState(24);
				subtype();
				}
				break;
			}
			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(28);
				match(T__4);
				setState(29);
				parameter();
				}
				}
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public TerminalNode TOKEN() { return getToken(AcceptHeaderParser.TOKEN, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).exitType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			match(TOKEN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SubtypeContext extends ParserRuleContext {
		public TerminalNode TOKEN() { return getToken(AcceptHeaderParser.TOKEN, 0); }
		public SubtypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).enterSubtype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).exitSubtype(this);
		}
	}

	public final SubtypeContext subtype() throws RecognitionException {
		SubtypeContext _localctx = new SubtypeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_subtype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			match(TOKEN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParameterContext extends ParserRuleContext {
		public List<TerminalNode> TOKEN() { return getTokens(AcceptHeaderParser.TOKEN); }
		public TerminalNode TOKEN(int i) {
			return getToken(AcceptHeaderParser.TOKEN, i);
		}
		public TerminalNode QVALUE() { return getToken(AcceptHeaderParser.QVALUE, 0); }
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AcceptHeaderListener ) ((AcceptHeaderListener)listener).exitParameter(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_parameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			match(TOKEN);
			setState(40);
			match(T__5);
			setState(41);
			_la = _input.LA(1);
			if ( !(_la==TOKEN || _la==QVALUE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\t,\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u000e\b\u0000\n\u0000\f\u0000"+
		"\u0011\t\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u001b\b\u0001\u0001\u0001"+
		"\u0001\u0001\u0005\u0001\u001f\b\u0001\n\u0001\f\u0001\"\t\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004\u0006"+
		"\b\u0000\u0001\u0001\u0000\u0007\b*\u0000\n\u0001\u0000\u0000\u0000\u0002"+
		"\u001a\u0001\u0000\u0000\u0000\u0004#\u0001\u0000\u0000\u0000\u0006%\u0001"+
		"\u0000\u0000\u0000\b\'\u0001\u0000\u0000\u0000\n\u000f\u0003\u0002\u0001"+
		"\u0000\u000b\f\u0005\u0001\u0000\u0000\f\u000e\u0003\u0002\u0001\u0000"+
		"\r\u000b\u0001\u0000\u0000\u0000\u000e\u0011\u0001\u0000\u0000\u0000\u000f"+
		"\r\u0001\u0000\u0000\u0000\u000f\u0010\u0001\u0000\u0000\u0000\u0010\u0001"+
		"\u0001\u0000\u0000\u0000\u0011\u000f\u0001\u0000\u0000\u0000\u0012\u001b"+
		"\u0005\u0002\u0000\u0000\u0013\u0014\u0003\u0004\u0002\u0000\u0014\u0015"+
		"\u0005\u0003\u0000\u0000\u0015\u001b\u0001\u0000\u0000\u0000\u0016\u0017"+
		"\u0003\u0004\u0002\u0000\u0017\u0018\u0005\u0004\u0000\u0000\u0018\u0019"+
		"\u0003\u0006\u0003\u0000\u0019\u001b\u0001\u0000\u0000\u0000\u001a\u0012"+
		"\u0001\u0000\u0000\u0000\u001a\u0013\u0001\u0000\u0000\u0000\u001a\u0016"+
		"\u0001\u0000\u0000\u0000\u001b \u0001\u0000\u0000\u0000\u001c\u001d\u0005"+
		"\u0005\u0000\u0000\u001d\u001f\u0003\b\u0004\u0000\u001e\u001c\u0001\u0000"+
		"\u0000\u0000\u001f\"\u0001\u0000\u0000\u0000 \u001e\u0001\u0000\u0000"+
		"\u0000 !\u0001\u0000\u0000\u0000!\u0003\u0001\u0000\u0000\u0000\" \u0001"+
		"\u0000\u0000\u0000#$\u0005\u0007\u0000\u0000$\u0005\u0001\u0000\u0000"+
		"\u0000%&\u0005\u0007\u0000\u0000&\u0007\u0001\u0000\u0000\u0000\'(\u0005"+
		"\u0007\u0000\u0000()\u0005\u0006\u0000\u0000)*\u0007\u0000\u0000\u0000"+
		"*\t\u0001\u0000\u0000\u0000\u0003\u000f\u001a ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}