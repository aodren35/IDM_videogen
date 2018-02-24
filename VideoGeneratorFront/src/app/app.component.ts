import {Component, OnInit} from '@angular/core';
import {VignettesService} from "./shared/vignettes.service";
import {NguCarousel, NguCarouselService, NguCarouselStore} from "@ngu/carousel";
import * as myGlobals from './app.config';
import * as FileSaver from "file-saver";


@Component({
  selector: 'vg-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  private title = 'vg';
  public vignettes: {url: string, name: string, selected: boolean}[] = [
    // "http://localhost:3000/vignettes/example10_0.jpg"
  ];
  private loaded = false;
  private carouselToken: string;
  public carouselTile: NguCarousel;

  public videoUrl = "";
  public baseStaticUrl = myGlobals.baseStaticUrl;

  videoUploaded = false;
  clicked = false;
  gifDownloading = false;

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
    console.log("INIT", this.vignettes)
    if (!this.loaded) {
      this.vignettesService.getAllVignettes().subscribe(
        (response: { url: string, name: string, selected: boolean }[]) => {
          for (const r of response) {
            this.vignettes.push(r);
          }
          this.carousel.reset(this.carouselToken);
          this.loaded = true;
        }
      );
    }
  }

  resetFn() {
    this.carousel.reset(this.carouselToken);
  }

  moveToSlide() {
    this.carousel.moveToSlide(this.carouselToken, 2, false);
  }


  public carouselTileLoad(evt: any) {
    console.log("TEST");

  }

  getVideo() {
    this.clicked = true;
    this.videoUploaded = false;
    this.vignettesService.getVariante().subscribe(
      (response) => {
        this.videoUrl = response;
        this.videoUploaded = true;
      }
    );
  }

  getGif() {
    this.gifDownloading = true;
    this.vignettesService.getGif(this.videoUrl).subscribe(
      response => {
        FileSaver.saveAs(response,"Gif_" + this.videoUrl+ ".gif");
        const fileUrl = URL.createObjectURL(response);
        window.open(fileUrl);
        this.gifDownloading = false;
      }
    );
  }

  select(v: any) {
    v.selected = !v.selected;
  }
}
