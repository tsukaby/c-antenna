module.exports = function (grunt) {
  require("time-grunt")(grunt);

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    opt: {
      client: {
        tsMain: "ui/scripts",
        tsTest: "ui/test",
        scss: "ui/scss",

        jsMainOut: "ui/scripts",
        jsTestOut: "ui/test",
        jsEspowerOut: "ui/testEspowered",
        cssOut: "ui/stylesheets",
        imageOut: "ui/images"
      }
    },

    ts: {
      options: {
        compile: true,                 // perform compilation. [true (default) | false]
        comments: false,               // same as !removeComments. [true | false (default)]
        target: 'es5',                 // target javascript language. [es3 (default) | es5]
        module: 'commonjs',            // target javascript module style. [amd (default) | commonjs]
        noImplicitAny: true,
        sourceMap: true,              // generate a source map for every output js file. [true (default) | false]
        sourceRoot: '',                // where to locate TypeScript files. [(default) '' == source ts location]
        mapRoot: '',                   // where to locate .map.js files. [(default) '' == generated js location.]
        declaration: false             // generate a declaration .d.ts file for every output js file. [true | false (default)]
      },
      clientMain: {
        src: ['<%= opt.client.tsMain %>/Ignite.ts'],
        out: '<%= opt.client.jsMainOut %>/Ignite.js'
      },
      clientTest: {
        src: ['<%= opt.client.tsTest %>/IgniteSpec.ts'],
        out: '<%= opt.client.jsTestOut %>/IgniteSpec.js'
      }
    },
    compass: {
      dev: {
        options: {
          sassDir: '<%= opt.client.scss %>',
          cssDir: '<%= opt.client.cssOut %>',
          imagesDir: '<%= opt.client.imageOut %>',
          javascriptsDir: '<%= opt.client.jsMainOut %>',
          noLineComments: false,
          debugInfo: true,
          relativeAssets: true
        }
      },
      prod: {
        options: {
          environment: 'production',
          sassDir: '<%= opt.client.scss %>',
          cssDir: '<%= opt.client.cssOut %>',
          imagesDir: '<%= opt.client.imageOut %>',
          javascriptsDir: '<%= opt.client.jsMainOut %>',
          noLineComments: true,
          debugInfo: false,
          relativeAssets: true
        }
      }
    },
    tslint: {
      options: {
        formatter: "prose",
        configuration: grunt.file.readJSON("tslint.json")
      },
      files: {
        src: [
          '<%= opt.client.tsMain %>/**/*.ts',
          '<%= opt.client.tsTest %>/**/*.ts'
        ]
      }
    },
    typedoc: {
      main: {
        options: {
          // module: "<%= ts.options.module %>",
          out: './docs',
          name: '<%= pkg.name %>',
          target: '<%= ts.options.target %>'
        },
        src: [
          '<%= opt.client.tsMain %>/**/*.ts'
        ]
      }
    },
    espower: {
      client: {
        files: [
          {
            expand: true,				// Enable dynamic expansion.
            cwd: '<%= opt.client.jsTestOut %>/',				// Src matches are relative to this path.
            src: ['**/*.js'],		// Actual pattern(s) to match.
            dest: '<%= opt.client.jsEspowerOut %>/',	// Destination path prefix.
            ext: '.js'					 // Dest filepaths will have this extension.
          }
        ]
      }
    },
    bower: {
      install: {
        options: {
          install: true,
          copy: false,
          verbose: true, // ログの詳細を出すかどうか
          cleanBowerDir: false
        }
      }
    },
    wiredep: {
      main: {
        src: ['index.html'], // point to your HTML file.
        exclude: []
      }
    },
    tsd: {
      client: {
        options: {
          // execute a command
          command: 'reinstall',

          //optional: always get from HEAD
          latest: false,

          // optional: specify config file
          config: './tsd.json'
        }
      }
    },
    clean: {
      clientCss: {
        src: [
          '<%= opt.client.cssOut %>/**/*.css'
        ]
      },
      clientScript: {
        src: [
          // client
          '<%= opt.client.jsMainOut %>/**/*.js',
          '<%= opt.client.jsMainOut %>/**/*.d.ts',
          '<%= opt.client.jsMainOut %>/**/*.js.map',
          // client test
          '<%= opt.client.jsTestOut %>/*.js',
          '<%= opt.client.jsTestOut %>/*.js.map',
          '<%= opt.client.jsTestOut %>/*.d.ts',
          '<%= opt.client.jsEspowerOut %>/'
        ]
      },
      tsd: {
        src: [
          // tsd installed
          'ui/typings'
        ]
      },
      bower: {
        src: [
          // bower installed
          'ui/bower_components'
        ]
      },
      play: {
        src: [
          'public'
        ]
      }
    },
    copy: {
      main: {
        expand: true,
        cwd: 'ui',
        src: '**',
        dest: 'public/'
      }
    },
    karma: {
      unit: {
        options: {
          configFile: 'karma.conf.js',
          autoWatch: false,
          browsers: ['PhantomJS'],
          reporters: ['progress', 'junit'],
          singleRun: true,
          keepalive: true
        }
      }
    },
    open: {
      "test-browser": {
        path: 'ui/test/SpecRunner.html'
      }
    },
    connect: {
      dev: {
        options: {
          port: 9000,
          base: "./",
          keepalive: true
        }
      }
    }
  });

  grunt.registerTask('setup', ['clean', 'bower', 'tsd', 'wiredep']);
  grunt.registerTask('default', ['clean:clientCss', 'clean:clientScript', 'clean:play', 'ts:clientMain', 'tslint', 'compass:dev', 'copy:main']);
  grunt.registerTask('test', ['clean:clientScript', 'ts:clientTest', 'tslint', 'espower', 'karma']);
  grunt.registerTask('docs', ['typedoc']);
  grunt.registerTask('serve', ['connect:dev']);

  require('load-grunt-tasks')(grunt);
};
