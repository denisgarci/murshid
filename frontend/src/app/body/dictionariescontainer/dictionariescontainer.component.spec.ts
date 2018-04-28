import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DictionariescontainerComponent } from './dictionariescontainer.component';

describe('DictionariescontainerComponent', () => {
  let component: DictionariescontainerComponent;
  let fixture: ComponentFixture<DictionariescontainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DictionariescontainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DictionariescontainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
