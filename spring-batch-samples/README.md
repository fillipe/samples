# Projeto base para treinamento de Spring-batch

- Passo a passo para configurar seu hsqldb:

1) Faça o download da versão 2.4.0 do hsqldb: https://sourceforge.net/projects/hsqldb/files/hsqldb/hsqldb_2_4/hsqldb-2.4.0.zip/download

2) Descompacte o hsqldb-2.4.0.zip em algum diretorio que você saiba onde está

3) Abra o prompt de comando no diretório onde você descompactou o hsqldb e entre o seguinte comando para subir o servidor: java -classpath hsqldb/lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:treinamentos/treinamento-batch --dbname.0 treinamento-batch

4) Abra a sua IDE favorita para se conectar ao HSQLDB. Se você não souber de uma ou não tiver uma, não tem problema, você pode abrir outro prompt de comando na raiz do diretório de seu HSQLDB e rodar o seguinte comando: 
java -classpath lib/hsqldb.jar org.hsqldb.util.DatabaseManagerSwing 
Na janela que abrirá, preencha apenas os campos:
Setting Name: Um nome que você quiser
URL: jdbc:hsqldb:hsql://localhost:9001/treinamento-batch

5) Em caso de dúvidas ou problemas não previstos, segue os links do guia oficial: 
http://hsqldb.org/web/usagelinks.html
http://www.programmingforfuture.com/2010/06/using-hypersql-hsqldb.html

6) Dentro deste projeto existe dentro da pasta Scripts um script que cria todas as tabelas necessárias para este treinamento. 

7) Após criadas as tabelas, execute a classe main br.com.fill.samples.data.generator.InsertGenerator.java para gerar a massa de dados necessária para rodar o job.
