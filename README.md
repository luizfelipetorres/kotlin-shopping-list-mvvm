# :robot::iphone: Shopping List

Projeto feito para por em prática os conceitos aprendidos nos cursos de Kotlin e Android (e também
para utilizar o app no dia a dia)

## :books: Conceitos estudados

- Programação android em Kotlin
- Arquitetura MVVM (Model-View-ViewModel)
- Repository pattern
- Room Database
- Navigation Component

## Funcionalidades existentes

Todas as entidades a baixo são armazenadas no armazenamento local do dispositivo móvel

- ### :page_facing_up: Lista de compras

  - Criar uma lista de compras, onde os itens tem os seguintes atributos:
    - Nome/descrição (string)
    - Valor (float)
    - Quantidade (int)
  - O valor total do item (valor * quantidade) é calculado automaticamente
  - Visualizar o total da sua lista de compras no AppBar (canto superior direito)
  - Excluir os itens da lista um a um, arrastando para o lado direito
  - Editar os itens da lista clicando e editando o formulário
  - Ordenar a lista de compras por ordem alfabética (crescente e descrescente)
  - Ordenar a lista de compras pelo valor (crescente e descrescente)
  - Itens ainda não adicionados no carrinho são destacados em vermelho
- ### :dollar: Formas de pagamento

  - Inserir uma lista de formas de pagamento, onde os itens tem os seguintes atributos:
    - Forma de pagamento (string)
    - Limite (float)
- ### 📺 Demo

<details>

<summary>Inserir</summary>

<image src="gifs/insert.gif" height=800rem>

</details>

<details>

<summary>Deletar</summary>

<image src="gifs/delete.gif" height=800rem>

</details>

<details>

<summary>Ordenar</summary>

<image src="gifs/sort.gif" height=800rem>

</details>

<details>

<summary>Adicionar forma de pagamento</summary>

<image src="gifs/add-payment-method.gif" height=800rem>

</details>

<details>

<summary>Editar</summary>

<image src="gifs/edit.gif" height=800rem>

</details>
