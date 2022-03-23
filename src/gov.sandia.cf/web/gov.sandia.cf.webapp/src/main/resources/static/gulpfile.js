//static/gulpfile.js
"use strict";

let gulp = require('gulp');
// let jshint = require('gulp-jshint');
let concat = require('gulp-concat');
let uglify = require('gulp-uglify');
let rename = require('gulp-rename');
// let babel = require('gulp-babel');

// Concatenate CSS
gulp.task('css', function () {
    return gulp.src([
        'node_modules/mdbootstrap/css/bootstrap.min.css',
        'node_modules/mdbootstrap/css/mdb.min.css',
        'node_modules/mdbootstrap/css/style.css',
        'node_modules/katex/dist/katex.min.css',
        'node_modules/quill/dist/quill.snow.css',
        // 'node_modules/@fortawesome/fontawesome-free/css/all.min.css',
    ])
        .pipe(concat('vendor.css'))
        .pipe(gulp.dest('assets/css/'));
});


// Move fonts
gulp.task('fonts', function () {
    return gulp.src([
        'node_modules/katex/dist/fonts/*',
        // 'node_modules/@fortawesome/fontawesome-free/webfonts/*',
    ])
        .pipe(gulp.dest('assets/fonts/'));
});

// Lint JS Task
// gulp.task('lint', function () {
//     return gulp.src('js/*.js')
//         .pipe(jshint())
//         .pipe(jshint.reporter('default'));
// });

// Concatenate & Minify JS
gulp.task('js', function () {
    return gulp.src([
        'node_modules/mdbootstrap/js/popper.min.js',
        'node_modules/mdbootstrap/js/jquery.min.js',
        'node_modules/mdbootstrap/js/bootstrap.min.js',
        'node_modules/mdbootstrap/js/mdb.min.js',
        'node_modules/quill/dist/quill.min.js',
        'node_modules/katex/dist/katex.min.js',
        // 'node_modules/@fortawesome/fontawesome-free/js/all.min.js',
    ])
        // .pipe(babel({
        //     presets: ['env']
        // }))
        .pipe(concat('vendor.js'))
        .pipe(gulp.dest('assets/js/'))
        .pipe(rename('vendor.min.js'))
        .pipe(uglify().on('error', function (e) {
            console.dir(e);
        }))
        .pipe(gulp.dest('assets/js/'));
});

// Build task
gulp.task('build', gulp.series(['css', 'fonts', 'js']));