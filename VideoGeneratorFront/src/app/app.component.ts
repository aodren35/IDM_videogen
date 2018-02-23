import {Component, OnInit} from '@angular/core';
import {VignettesService} from "./shared/vignettes.service";
import {NguCarousel, NguCarouselService, NguCarouselStore} from "@ngu/carousel";


@Component({
  selector: 'vg-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  private title = 'vg';
  private vignettes: string[] = [
    // "http://localhost:3000/vignettes/example10_0.jpg"
  ];
  private loaded = false;
  private carouselToken: string;
  public carouselTile: NguCarousel;

  constructor(
    private vignettesService: VignettesService,
    private carousel: NguCarouselService
    ) {}
  ngOnInit() {

   /* this.vignettesService.getAllVignettes().subscribe(
      (response) => {
        this.vignettes.push(response);
        this.carousel.reset(this.carouselToken);
        this.loaded = true;
      }
    );*/
    this.carouselTile = {
      grid: {xs: 2, sm: 3, md: 3, lg: 5, all: 0},
      slide: 2,
      speed: 400,
      animation: 'lazy',
      point: {
        visible: true
      },
      load: 2,
      touch: true,
      easing: 'ease'
    };
  }

  initDataFn(key: NguCarouselStore) {
    this.carouselToken = key.token;
    console.log("INIT")
    this.vignettesService.getAllVignettes().subscribe(
      (response) => {
        console.log(response)
        for (const r of response) {
          this.vignettes.push(r);
        }
        this.carousel.reset(this.carouselToken);
        this.loaded = true;
      }
    );
  }

  resetFn() {
    this.carousel.reset(this.carouselToken);
  }

  moveToSlide() {
    this.carousel.moveToSlide(this.carouselToken, 2, false);
  }


  public carouselTileLoad(evt: any) {
    console.log("TEST")
    this.vignettesService.getAllVignettes().subscribe(
      (response) => {
        for (const r of response) {
          this.vignettes.push(r);
        }
        this.carousel.reset(this.carouselToken);
        this.loaded = true;
      }
    );

  }
}
