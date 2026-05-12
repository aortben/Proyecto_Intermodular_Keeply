import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslateModule } from '@ngx-translate/core';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [NavbarComponent, TranslateModule],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent { }