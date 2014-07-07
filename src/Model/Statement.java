package Model;

import java.io.Serializable;

/**
 * Istniej¹ce konto (nie myliæ z profilem) mo¿e mieæ status zalogowanego lub niezalogowanego NA SERWERZE
 * @author necia
 *
 */
public enum Statement implements Serializable {
	LOGGED, UNLOGGED;
}

