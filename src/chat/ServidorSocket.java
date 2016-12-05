
package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
public class ServidorSocket extends Thread {
    //Controla as conexões por meio das THREADS
    private static Vector CLIENTES;
    //Socket do cliente
    private Socket conexao;
    //nome do cliente
    private String nomeCliente;
    //Lista que armazena nome dos clientes
    private static List LISTA_DE_NOMES = new ArrayList();
    //Construtor = recebe o socket desse cliente
    public ServidorSocket(Socket socket) {
        this.conexao = socket;
    }
    //Testa se os nomes são iguais. Se forem recebe TRUE
    public boolean armazena(String newName){
       //System.out.println(LISTA_DE_NOMES);
       for (int i=0; i < LISTA_DE_NOMES.size(); i++){
         if(LISTA_DE_NOMES.get(i).equals(newName))
           return true;
       }
       //Adiciona na lista se não existir
       LISTA_DE_NOMES.add(newName);
       return false;
    }
    //Remove da lista os clientes que deixaram o chat
    public void remove(String oldName) {
       for (int i=0; i< LISTA_DE_NOMES.size(); i++){
         if(LISTA_DE_NOMES.get(i).equals(oldName))
           LISTA_DE_NOMES.remove(oldName);
       }
    }
    public static void main(String args[]) {
        //Instancia vetor de clientes conectados
        CLIENTES = new Vector();
        try {
            //cria um socket escutando a porta 5555
            ServerSocket server = new ServerSocket(5555);
            System.out.println("ServidorSocket rodando na porta 5555");
            
            while (true) {
                //Aguarda um cliente se conectar
                //A execução fica bloqueada na chamada do método accept da classe ServidorSocket até
                //um cliente conectar-se ao servidor, quando o próprios método desbloqueia e retorna com um 
                //objeto da classe Socket
                Socket conexao = server.accept();
                //Cria uma THREAD que trata essa conexão
                Thread t = new ServidorSocket(conexao);
                t.start();
                //Volta ao início aguardando nova conexão de outr cliente
            }
        } catch (IOException e) {
            //Caso ocorra exceção de Entrada/Saída, a mostra 
            System.out.println("IOException: " + e);
        }
    }
    //Execução
    public void run()
    {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            PrintStream saida = new PrintStream(this.conexao.getOutputStream());
            //Nome do cliente
            this.nomeCliente = entrada.readLine();
            //Testa nomes iguais
            if (armazena(this.nomeCliente)){
              saida.println("Este nome ja existe! Conecte novamente com outro Nome.");
              CLIENTES.add(saida);
              //fecha a conexao com este cliente
              this.conexao.close();
              return;
            } else {
               //mostra o nome do cliente conectado ao servidor
               System.out.println(this.nomeCliente + " : Conectado ao Servidor!");
            }
            //se igual a null encerra a conexão
            if (this.nomeCliente == null) {
                return;
            }
            //adiciona os dados de saida do cliente no objeto CLIENTES
            CLIENTES.add(saida);
            //recebe a mensagem do cliente
            String msg = entrada.readLine();
            // Verificar se linha é null (conexão encerrada)
            // Se não for nula, mostra a troca de mensagens entre os CLIENTES
            while (msg != null && !(msg.trim().equals("")))
            {
                // reenvia a linha para todos os CLIENTES conectados
                sendToAll(saida, " escreveu: ", msg);
                // espera por uma nova linha.
                msg = entrada.readLine();
            }
            //se cliente enviar linha em branco, mostra a saida no servidor
            System.out.println(this.nomeCliente + " saiu do bate-papo!");
            //se cliente enviar linha em branco, servidor envia  mensagem de saida do chat aos CLIENTES conectados
            sendToAll(saida, " saiu", " do bate-papo!");
            //remove nome da lista
            remove(this.nomeCliente);
            //exclui atributos setados ao cliente
            CLIENTES.remove(saida);
            //fecha a conexao com este cliente
            this.conexao.close();
        } catch (IOException e) {
            // Caso ocorra alguma excessão de E/S, mostre qual foi.
            System.out.println("Falha na Conexao... .. ."+" IOException: " + e);
        }
    }
    // enviar uma mensagem para todos, menos para o próprio
    public void sendToAll(PrintStream saida, String acao, String msg) throws IOException {
        Enumeration e = CLIENTES.elements();
        while (e.hasMoreElements()) {
            // obtém o fluxo de saída de um dos CLIENTES
            PrintStream chat = (PrintStream) e.nextElement();
            // envia para todos, menos para o próprio usuário
            if (chat != saida) {
                chat.println(this.nomeCliente + acao + msg);
            }
        }
      }
}
