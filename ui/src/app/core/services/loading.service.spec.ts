import { TestBed } from '@angular/core/testing';
import { LoadingService } from './loading.service';

describe('LoadingService (Serviço de Carregamento)', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoadingService]
    });
    service = TestBed.inject(LoadingService);
  });

  it('deve iniciar com o estado de carregamento como falso', () => {
    expect(service.isLoading()).toBeFalse();
  });

  it('deve definir isLoading como verdadeiro quando show é chamado', () => {
    service.show();
    expect(service.isLoading()).toBeTrue();
  });

  it('deve decrementar o contador e retornar a falso quando hide é chamado', () => {
    service.show();
    expect(service.isLoading()).toBeTrue();

    service.hide();
    expect(service.isLoading()).toBeFalse();
  });

  it('deve gerenciar requisições concorrentes corretamente', () => {
    service.show();
    service.show();
    expect(service.isLoading()).toBeTrue();

    service.hide();
    expect(service.isLoading()).toBeTrue();

    service.hide();
    expect(service.isLoading()).toBeFalse();
  });

  it('não deve decrementar o contador abaixo de zero', () => {
    service.hide();
    expect(service.isLoading()).toBeFalse();
  });
});
