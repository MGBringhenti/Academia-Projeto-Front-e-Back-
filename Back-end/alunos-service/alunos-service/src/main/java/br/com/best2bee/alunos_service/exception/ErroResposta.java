package br.com.best2bee.alunos_service.exception;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter 
public class ErroResposta {
    private LocalDateTime  timestamp;
    private Integer status;
    private String erro;
    private String mensagem;


    public ErroResposta(Integer status, String erro, String mensagem){
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        
    }
}