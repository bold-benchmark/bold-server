/**
 * grammar for parsing the HTTP Accept header field set by clients (for content negotiation).
 *
 * See https://datatracker.ietf.org/doc/html/rfc7231#section-5.3
 */
grammar AcceptHeader;

@header {
package org.bold.conneg.parser;
}

accept : mediaRange ((OWS? ',' OWS?) mediaRange )*;

mediaRange : ( '*/*' | type '/*' | type '/' subtype ) ( OWS? ';' OWS? parameter )* ;

type : TOKEN ;

subtype : TOKEN ;

parameter : TOKEN '=' (QVALUE | TOKEN) ; // TODO or quoted string

TOKEN : [\-+0-9a-zA-Z]+;

QVALUE : ('0' ('.' [0-9]*)?)
       | ('1' ('.' '0'*)?);

OWS : [ \t\r\n] -> skip ; // optional white space