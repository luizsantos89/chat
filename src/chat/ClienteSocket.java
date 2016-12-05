
package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClienteSocket extends Thread {
    // parte que controla a recep&ccedil;&atilde;o de mensagens do cliente
    private Socket conexao;
    // construtor que recebe o socket do cliente
    public ClienteSocket(Socket socket) {
        this.conexao = socket;
    }
    public static void main(String args[])
    {
        try {
            //Socket com IP e porta do servidor
            Socket socket = new Socket("127.0.0.1", 5555);
            //Controla o fluxo de execução
            PrintStream saida = new PrintStream(socket.getOutputStream());
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Digite seu nome: ");
            String meuNome = teclado.readLine();
            //Nome do cliente
            saida.println(meuNome.toUpperCase());
            //Inicia a Thread 
            Thread thread = new ClienteSocket(socket);
            thread.start();
            //Variável que envia a msg para o servidor e mostra para todos os outros usuários
            String msg;
            while(true) {
                //Linha para digitação da mensagem
                System.out.print("Mensagem > ");
                msg = teclado.readLine();
                //Envia a mensagem para o servidor
                saida.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }
    //Execução da Thread
    public void run()
    {
        try {
            //Recebe as mensagens através do servidor
            BufferedReader entrada = 
		new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            //Cria variável msg
            String msg;
            while (true)
            {
                //Pega o que o servidor enviou
                msg = entrada.readLine();
                //Se houver mensagem entra no IF
                if (msg == null) {
                    System.out.println("Conex&atilde;o encerrada!");
                    System.exit(0);
                }
                System.out.println();
                //Imprime no console a mensagem recebida 
                System.out.println(msg);
                //Cria uma linha para a mensagem
                System.out.print("Responder > ");
            }
        } catch (IOException e) {
            //Se houver exceção de Entrada/Saida, a mostra
            System.out.println("Ocorreu uma Falha... .. ." + 
				" IOException: " + e);
        }
    }
}