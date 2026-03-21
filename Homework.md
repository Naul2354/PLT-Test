ok based on 2 test that student managment and adding course test . i want add new homework ( belongs to user test ) .https://elearning.plt.pro.vn/quan-tri-vien/bai-tap 
* workflow test 
* log in like 2 test use same creds , navigate to https://elearning.plt.pro.vn/quan-tri-vien/bai-tap  or click "Quản lý bài tập"
* Click button Add new ( vietnamese is Thêm mới ) -> It pop-up on right-side 1 text-box ( Name of Homework ( vietnamese is Tên bài tập ) -> Input random name about Homework ) 
* Second is when click #formUpdateAddNew > div > div.col-sm-4.col-12 > div.v-card.v-sheet.theme--light > div.v-card__text > div -> it pop up a file then input file cursor.png then enter ( it will different with Window , i use ubuntu ) but same logic that enter cursor.png in Search box then click ENTER 
* next is click button "Add new question" ( vietnamese is thêm câu hỏi ) <button data-v-7eced93c="" type="button" class="v-btn v-btn--is-elevated v-btn--has-bg theme--light v-size--default primary" style="height: 36px;"><span class="v-btn__content"><i data-v-7eced93c="" aria-hidden="true" class="v-icon notranslate mdi mdi-plus theme--light"></i>
        Thêm câu hỏi
      </span></button>
* then click "mũi tên" to expand -> Next will see Nội dung câu hỏi , and 4 text box name Câu trả lời -> Input 4 answer into each text-box , next is mark box ( tick it for student understand what answer is right , can random tick 1,2,3 box ) -> then click expand again
* same with it repeat step add new questions ( Thêm câu hỏi , add 1 or 2 if need ) -> BASED ON JSON OR CSV WE WILL INPUT LATER 
* next is harder that when drop down next <button data-v-7eced93c="" type="button" class="v-btn v-item--active v-btn--active v-btn--icon v-btn--round theme--light v-size--default primary--text" role="button" aria-haspopup="true" aria-expanded="false" style="height: 36px;"><span class="v-btn__content"><i data-v-7eced93c="" aria-hidden="true" class="v-icon notranslate mdi mdi-menu-down theme--light"></i></span></button>
-> will see 

<div data-v-7eced93c="" class="v-list v-sheet theme--light"><div data-v-7eced93c="" tabindex="0" role="menuitem" id="list-item-1261" class="v-list-item v-list-item--link theme--light"><div data-v-7eced93c="" class="v-list-item__title">Âm thanh</div></div><div data-v-7eced93c="" tabindex="0" role="menuitem" id="list-item-1262" class="v-list-item v-list-item--link theme--light"><div data-v-7eced93c="" class="v-list-item__title">Hình ảnh</div></div><div data-v-7eced93c="" tabindex="0" role="menuitem" id="list-item-1263" class="v-list-item v-list-item--link theme--light"><div data-v-7eced93c="" class="v-list-item__title">Video</div></div><div data-v-7eced93c="" tabindex="0" role="menuitem" id="list-item-1264" class="v-list-item v-list-item--link theme--light"><div data-v-7eced93c="" class="v-list-item__title">Tự luận</div></div></div>
include : âm thanh , hình ảnh , video , tự luận -> same with 4 answers but different function . 
1. âm thanh -> click it will AUTO add new questions -> THEN EXPAND IT 
-> <div class="v-input__control"><div class="v-input__slot"><div class="v-text-field__slot"><label for="input-1401" class="v-label theme--light" style="left: 0px; right: auto; position: absolute;">File âm thanh của câu hỏi</label><div class="v-file-input__text"></div><input accept=".mp3,audio/*" id="input-1401" type="file"></div><div class="v-input__append-inner"><div></div></div></div><div class="v-text-field__details"><div class="v-messages theme--light"><div class="v-messages__wrapper"></div></div></div></div>
<div class="v-input__slot"><div class="v-text-field__slot"><label for="input-1401" class="v-label theme--light" style="left: 0px; right: auto; position: absolute;">File âm thanh của câu hỏi</label><div class="v-file-input__text"></div><input accept=".mp3,audio/*" id="input-1401" type="file"></div><div class="v-input__append-inner"><div></div></div></div>
<label for="input-1401" class="v-label theme--light" style="left: 0px; right: auto; position: absolute;">File âm thanh của câu hỏi</label>
-> it will popup window to select file mp4 ( i will add later ) 
** nội dung câu hỏi ( same with above ) , answer .... 
2. hình ảnh 
* same with âm thanh that when click hình ảnh it auto create 1 questions with features different function
first when we click File hình ảnh của câu hỏi -> same with Âm thanh -> it will pop-up window to select image 
<div data-v-bde4f360="" class="v-input theme--light v-text-field v-text-field--is-booted v-file-input"><div class="v-input__prepend-outer"><div class="v-input__icon v-input__icon--prepend"><button type="button" aria-label="prepend icon" class="v-icon notranslate v-icon--link mdi mdi-camera theme--light"></button></div></div><div class="v-input__control"><div class="v-input__slot"><div class="v-text-field__slot"><label for="input-1354" class="v-label theme--light" style="left: 0px; right: auto; position: absolute;">File hình ảnh của câu hỏi</label><div class="v-file-input__text"></div><input accept="image/png, image/jpeg, image/bmp" id="input-1354" type="file"></div><div class="v-input__append-inner"><div></div></div></div><div class="v-text-field__details"><div class="v-messages theme--light"><div class="v-messages__wrapper"></div></div></div></div></div>
** nội dung câu hỏi ( same with above ) , and answer and tick to box to mark what answer is right ( same ) 
3. video : same 
it is text-box ( can random add link youtube later )
<label for="input-1306" class="v-label v-label--active theme--light" style="left: 0px; right: auto; position: absolute;">ID video trên youtube.com</label>
nội dung câu hỏi and answer and tick box same with âm thanh and hình ảnh
4. tự luận
incldue : nội dung câu hỏi and 1 text box số lượng ký tự cho phép trả lời ( limit number allow answer -> have 2 actions ( 1 can input a number , 2 is raise or down number what you want ) .

Final is <i data-v-0fab9222="" aria-hidden="true" class="v-icon notranslate v-icon--left mdi mdi-content-save theme--light"></i> -> to finish this step . 
* 