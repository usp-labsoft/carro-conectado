# carro-conectado

O projeto utiliza uma integração com o OBD-II, presente em todos os carros atuais, para a agregação de dados e geração de dados estatísticos a respeito da conduta de motoristas sobre seus veículos.

O OBD-II é conectado a um Arduino, o qual envia ao OBD-II os comandos de requisição	de velocidade corrente do carro. Tal requisição é realizada em intervalos de tempo pré definidos (atualmente, de 10 em 10 segundos).

O Arduino então envia tais dados a um aparelho Android pareado, que possui o aplicativo desenvolvido instalado em si. A partir disso, o aplicativo envia à API externa de requisição de limite de velocidade de vias um pedido contendo a latitude e longitude do celular no momento corrente.

Obtendo os dados de limite de velocidade, o aplicativo Android junta-os aos dados de velocidade recebidos do Arduino, e envia junto aos dados de id de usuário, de Arduino e o timestamp do instante corrente ao servidor.

O servidor por sua vez faz uma análise para decidir se os dados devem ser salvos ou não. São salvos dados com mais de um minuto de diferença entre o último dado salvo para tal usuário ou caso tenha havido mudança de mais de 3km/h na velocidade em relação ao último dado.

Há uma interface simples, que permite ao administrador do sistema o cadastro de novos usuários, verificação dos usuários já existentes, com os dados estatísticos referentes à qualidade de sua conduta, cadastro de novos Arduinos (relacionando-os ao usuário ao qual tal Arduino pertence), e verificação das entradas de navegação registradas no banco de dados.
