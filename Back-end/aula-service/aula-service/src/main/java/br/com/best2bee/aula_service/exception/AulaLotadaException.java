package br.com.best2bee.aula_service.exception;

public class AulaLotadaException extends RuntimeException{
    
    public AulaLotadaException(String mensagem){
        super(mensagem);
    }
}