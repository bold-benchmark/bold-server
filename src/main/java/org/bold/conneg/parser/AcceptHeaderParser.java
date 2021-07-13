// Generated from src/main/antlr/AcceptHeader.g4 by ANTLR 4.9.2

package org.bold.conneg;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AcceptHeaderParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

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

	public static class AcceptContext extends ParserRuleContext {
		public List<MediaRangeContext> mediaRange() {
			return getRuleContexts(MediaRangeContext.class);
		}
		public MediaRangeContext mediaRange(int i) {
			return getRuleContext(MediaRangeContext.class,i);
		}
		public List<TerminalNode> OWS() { return getTokens(AcceptHeaderParser.OWS); }
		public TerminalNode OWS(int i) {
			return getToken(AcceptHeaderParser.OWS, i);
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
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==OWS) {
				{
				{
				{
				setState(12);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OWS) {
					{
					setState(11);
					match(OWS);
					}
				}

				setState(14);
				match(T__0);
				setState(16);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OWS) {
					{
					setState(15);
					match(OWS);
					}
				}

				}
				setState(18);
				mediaRange();
				}
				}
				setState(23);
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
		public List<TerminalNode> OWS() { return getTokens(AcceptHeaderParser.OWS); }
		public TerminalNode OWS(int i) {
			return getToken(AcceptHeaderParser.OWS, i);
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
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(24);
				match(T__1);
				}
				break;
			case 2:
				{
				setState(25);
				type();
				setState(26);
				match(T__2);
				}
				break;
			case 3:
				{
				setState(28);
				type();
				setState(29);
				match(T__3);
				setState(30);
				subtype();
				}
				break;
			}
			setState(44);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(35);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==OWS) {
						{
						setState(34);
						match(OWS);
						}
					}

					setState(37);
					match(T__4);
					setState(39);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==OWS) {
						{
						setState(38);
						match(OWS);
						}
					}

					setState(41);
					parameter();
					}
					} 
				}
				setState(46);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
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
			setState(47);
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
			setState(49);
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
			setState(51);
			match(TOKEN);
			setState(52);
			match(T__5);
			setState(53);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13:\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\5\2\17\n\2\3\2\3\2\5\2\23\n\2\3\2\7"+
		"\2\26\n\2\f\2\16\2\31\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3#\n\3\3"+
		"\3\5\3&\n\3\3\3\3\3\5\3*\n\3\3\3\7\3-\n\3\f\3\16\3\60\13\3\3\4\3\4\3\5"+
		"\3\5\3\6\3\6\3\6\3\6\3\6\2\2\7\2\4\6\b\n\2\3\3\2\t\n\2<\2\f\3\2\2\2\4"+
		"\"\3\2\2\2\6\61\3\2\2\2\b\63\3\2\2\2\n\65\3\2\2\2\f\27\5\4\3\2\r\17\7"+
		"\13\2\2\16\r\3\2\2\2\16\17\3\2\2\2\17\20\3\2\2\2\20\22\7\3\2\2\21\23\7"+
		"\13\2\2\22\21\3\2\2\2\22\23\3\2\2\2\23\24\3\2\2\2\24\26\5\4\3\2\25\16"+
		"\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2\2\30\3\3\2\2\2\31\27"+
		"\3\2\2\2\32#\7\4\2\2\33\34\5\6\4\2\34\35\7\5\2\2\35#\3\2\2\2\36\37\5\6"+
		"\4\2\37 \7\6\2\2 !\5\b\5\2!#\3\2\2\2\"\32\3\2\2\2\"\33\3\2\2\2\"\36\3"+
		"\2\2\2#.\3\2\2\2$&\7\13\2\2%$\3\2\2\2%&\3\2\2\2&\'\3\2\2\2\')\7\7\2\2"+
		"(*\7\13\2\2)(\3\2\2\2)*\3\2\2\2*+\3\2\2\2+-\5\n\6\2,%\3\2\2\2-\60\3\2"+
		"\2\2.,\3\2\2\2./\3\2\2\2/\5\3\2\2\2\60.\3\2\2\2\61\62\7\t\2\2\62\7\3\2"+
		"\2\2\63\64\7\t\2\2\64\t\3\2\2\2\65\66\7\t\2\2\66\67\7\b\2\2\678\t\2\2"+
		"\28\13\3\2\2\2\t\16\22\27\"%).";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}