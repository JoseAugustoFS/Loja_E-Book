package br.edu.iff.bsi.LojaEBook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.iff.bsi.LojaEBook.model.Cliente;
import br.edu.iff.bsi.LojaEBook.model.Colecao_E_Book;
import br.edu.iff.bsi.LojaEBook.model.Compra;
import br.edu.iff.bsi.LojaEBook.model.E_Book;
import br.edu.iff.bsi.LojaEBook.repository.ClienteRepository;
import br.edu.iff.bsi.LojaEBook.repository.Colecao_E_BookRepository;
import br.edu.iff.bsi.LojaEBook.repository.CompraRepository;
import br.edu.iff.bsi.LojaEBook.repository.E_BookRepository;

@Service
public class CompraService {

	@Autowired
	private CompraRepository CompraRep;
	@Autowired
	private ClienteRepository ClienteRep;
	@Autowired
	private Colecao_E_BookRepository Colecao_E_BookRep;
	@Autowired
	private E_BookRepository E_BookRep;

	public String addCompra(String cpf) {
		Cliente cl = ClienteRep.buscarPeloCPF(cpf);
		if(cl==null) {
			return "Cliente não achado";
		}else {
			if(CompraRep.BuscarComprasAbertasPeloCPF(cpf).size()==0) {				
				Compra compra = new Compra(cpf);
				cl.adicionarCompra(compra);
				Compra c = CompraRep.save(compra);
				ClienteRep.flush();
				return "Registrado no id "+c.getId();
			}else {
				return "O cliente já tem uma compra aberta";
			}
		}
	}


	public String atualizarCompra(Long idCompra, String cpf) {
		Compra c = CompraRep.BuscarPeloId(idCompra);
		if(c==null) {
			return "Compra não achada";
		}else {		
			if(c.isFinalizado()) {
				return "Compra já finalizada";
			}else {				
				if(cpf!=null) {				
					Cliente cl = ClienteRep.buscarPeloCPF(cpf);
					if(cl==null) {
						return "Cliente não achado";
					}else {
						c.setCpfCliente(cpf);
						cl.adicionarCompra(c);
						ClienteRep.flush();
					}
				}
				CompraRep.flush();
				return "Atualizado no id "+c.getId();
			}
		}
	}

	public String deletarCompra(Long idCompra) {
		Compra c = CompraRep.BuscarPeloId(idCompra);
		if(c==null) {
			return "Compra não achada";
		}else {		
			Long idCliente = CompraRep.BuscarPeloIdCliente(idCompra);
			Cliente cl = ClienteRep.BuscarPeloId(idCliente);
			if(cl==null) {
				return "Cliente não achado";
			}else {
				cl.removerCompra(c);
				ClienteRep.flush();
			}

			CompraRep.delete(c);
			return "Deletado no id "+c.getId();
		}
	}

	public List<Compra> listarCompras() throws Exception {
		return CompraRep.findAll();
	}

	public String addE_Book(String idCompra, String titulo) {
		Compra c = CompraRep.BuscarPeloId(Long.parseLong(idCompra));
		if(c==null) {
			return "Compra não encontrada";
		}else {			
			if(c.isFinalizado()) {
				return "Compra já finalizada";
			}else {				
				E_Book e = E_BookRep.buscarPeloTitulo(titulo);
				if(e==null) {
					return "E-Book não encontrado";
				}else {
					if(CompraRep.verificarProdutoCompra(e.getId(),c.getId())!=0) {
						return "E-Book já cadastrado";
					}else {					
						c.adicionarProduto(e);
						CompraRep.flush();
						return "E-Book adicionado";
					}
				}
			}
		}
	}

	public String removeE_Book(String idCompra, String titulo) {
		Compra c = CompraRep.BuscarPeloId(Long.parseLong(idCompra));
		if(c==null) {
			return "Compra não encontrada";
		}else {		
			E_Book e = E_BookRep.buscarPeloTitulo(titulo);
			if(e==null) {
				return "E-Book não encontrado";
			}else {
				if(CompraRep.verificarProdutoCompra(e.getId(),c.getId())==0) {
					return "E-Book não consta na compra";
				}else {
					c.removerProduto(e);
					CompraRep.flush();
					return "E-Book removido";
				}
			}
		}
	}

	public String addColecaoE_Book(String idCompra, String serie) {
		Compra c = CompraRep.BuscarPeloId(Long.parseLong(idCompra));
		if(c==null) {
			return "Compra não encontrada";
		}else {			
			if(c.isFinalizado()) {
				return "Compra já finalizada";
			}else {				
				Colecao_E_Book ce = Colecao_E_BookRep.buscarPelaSerie(serie);
				if(ce==null) {
					return "Coleção de E-Book não encontado";
				}else {
					if(CompraRep.verificarProdutoCompra(ce.getId(),c.getId())!=0) {
						return "Coleção de E-Book já cadastrado";
					}else {
						c.adicionarProduto(ce);
						CompraRep.flush();
						return "Coleção de E-Book adicionada";
					}
				}
			}
		}
	}

	public String removeColecaoE_Book(String idCompra, String serie) {
		Compra c = CompraRep.BuscarPeloId(Long.parseLong(idCompra));
		if(c==null) {
			return "Compra não encontrada";
		}else {		
			if(c.isFinalizado()) {
				return "Compra já finalizada";
			}else {				
				Colecao_E_Book ce = Colecao_E_BookRep.buscarPelaSerie(serie);;
				if(ce==null) {
					return "Coleção de E-Book não encontrado";
				}else {
					if(CompraRep.verificarProdutoCompra(ce.getId(),c.getId())==0) {
						return "Coleção de E-Book não consta na compra";
					}else {
						c.removerProduto(ce);
						CompraRep.flush();
						return "Coleção de E-Book removida";
					}
				}
			}
		}
	}

	public List<E_Book> ListarEBookPeloIdCompra(Long id){
		return E_BookRep.ListarEBookPeloIdCompra(id);
	}

	public List<Colecao_E_Book> ListarColecaoEBookPeloIdCompra(Long id){
		return Colecao_E_BookRep.ListarColecaoEBookPeloIdCompra(id);
	}

	public String finalizarCompraPeloId(Long idCompra) {
		Compra c = CompraRep.BuscarPeloId(idCompra);
		if(c==null) {
			return "Compra não encontrada";
		}else {	
			if(c.isFinalizado()) {
				return "Compra já finalizada";
			}else {
				if(c.getQtdProdutos()==0) {
					return "A compra precisa ter no mínimo 1 produto.";
				}else {
					Cliente cl = ClienteRep.BuscarPeloIdCompra(c.getId());
					if(cl.verSaldo()<c.getPrecoFinal()) {
						return "Saldo do cliente não é o suficiente";
					}else {						
						cl.removerSaldo(c.getPrecoFinal());
						c.finalizar();
						CompraRep.flush();
						ClienteRep.flush();
						return "Compra finalizada com sucesso";
					}
				}
			}
		}
	}

	public Compra getCompraById(Long id) {
		return CompraRep.BuscarPeloId(id);
	}

	public List<Compra> buscarPeloCPFCliente(String cpf) throws Exception {
		return CompraRep.BuscarPeloCPF(cpf);
	}

	public Compra CompraAbertaPeloCPFCliente(String cpf) throws Exception {
		List<Compra> compra = CompraRep.BuscarComprasAbertasPeloCPF(cpf);
		if(compra.size()==0) {
			this.addCompra(cpf);
			compra = CompraRep.BuscarComprasAbertasPeloCPF(cpf);
		}
		return compra.get(0);
	}
	
	public List<Compra> ListarFechadasPeloCPFCliente(String cpf) throws Exception {
		return CompraRep.BuscarComprasFechadasPeloCPF(cpf);
	}
}
