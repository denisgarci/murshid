import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationscontainerComponent } from './translationscontainer.component';

describe('TranslationscontainerComponent', () => {
  let component: TranslationscontainerComponent;
  let fixture: ComponentFixture<TranslationscontainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationscontainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationscontainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
