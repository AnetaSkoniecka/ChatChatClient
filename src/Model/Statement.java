package Model;

import java.io.Serializable;

/**
 * Istniej�ce konto (nie myli� z profilem) mo�e mie� status zalogowanego lub niezalogowanego NA SERWERZE
 * @author necia
 *
 */
public enum Statement implements Serializable {
	LOGGED, UNLOGGED;
}

