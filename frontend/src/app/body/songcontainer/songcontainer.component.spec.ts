import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SongcontainerComponent } from './songcontainer.component';

describe('SongcontainerComponent', () => {
  let component: SongcontainerComponent;
  let fixture: ComponentFixture<SongcontainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SongcontainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SongcontainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
