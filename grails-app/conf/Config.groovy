import org.apache.log4j.Level

def appName = "bie-index"

grails.project.groupId = "au.org.ala" // change this to alter the default package name and Maven publishing destination

default_config = "/data/${appName}/config/${appName}-config.properties"
if(!grails.config.locations || !(grails.config.locations instanceof List)) {
    grails.config.locations = []
}

if (new File(default_config).exists()) {
    println "[${appName}] Including default configuration file: " + default_config;
    grails.config.locations.add "file:" + default_config
} else {
    println "[${appName}] No external configuration file defined."
}

println "[${appName}] (*) grails.config.locations = ${grails.config.locations}"
println "default_config = ${default_config}"

indexLiveBaseUrl = "http://localhost:8080/solr/bie"
indexOfflineBaseUrl = "http://localhost:8080/solr/bie-offline"
biocacheService.baseUrl = "http://biocache.ala.org.au/ws"
biocache.solr.url="http://localhost:8080/solr/biocache"
defaultNameSourceAttribution = "National Species Lists"
commonNameSourceAttribution = "National Species Lists"
commonNameDefaultLanguage = "en-XX"
identifierSourceAttribution = "National Species Lists"
indexImages = true
importDir = "/data/bie/import"
collectoryUrl = "http://collections.ala.org.au/ws"
layersServicesUrl = "http://spatial.ala.org.au/ws"
imageThumbnailUrl = "http://images.ala.org.au/image/proxyImageThumbnail?imageId="
imageLargeUrl = "http://images.ala.org.au/image/proxyImage?imageId="
imageSmallUrl = "http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId="
synonymCheckingEnabled = true
synonymSourceAttribution = "National Species Lists"
gazetteerLayerId = "2123"
wordPress {
    sitemapUrl = "http://www.ala.org.au/sitemap.xml"
    baseUrl = "http://www.ala.org.au/?page_id="
    excludedCategories = ["button"]
    contentOnlyParams = "?content-only=1&categories=1"
}
speciesList.url = "http://lists.ala.org.au/ws/speciesListItems/"
speciesList.params = "?includeKVP=true"
// Conservation lists to index
conservationList.defaultSourceField = "status"
conservationLists {
    dr656 = [ field: "conservationStatusAUS_s", term: "conservationStatusAUS", label: "AUS"]
    dr649 = [ field: "conservationStatusACT_s", term: "conservationStatusACT", label: "ACT"]
    dr650 = [ field: "conservationStatusNSW_s", term: "conservationStatusNSW", label: "NSW"]
    dr652 = [ field: "conservationStatusQLD_s", term: "conservationStatusQLD", label: "QLD"]
    dr655 = [ field: "conservationStatusVIC_s", term: "conservationStatusVIC", label: "VIC", sourceField: 'status']
    dr654 = [ field: "conservationStatusTAS_s", term: "conservationStatusTAS", label: "TAS"]
    dr653 = [ field: "conservationStatusSA_s", term: "conservationStatusSA", label: "SA"]
    dr2201 = [ field: "conservationStatusWA_s", term: "conservationStatusWA", label: "WA"]
    dr651 = [ field: "conservationStatusNT_s", term: "conservationStatusNT", label: "NT"]
}
// Image lists to use, highest priority first
imageLists = [
    [ drUid: "dr4778", imageId: "imageId" ]
]
// Preferred images - these will be added as boosts to help rank good-looking pictures
imageBoosts = [
        "record_type:Image^10",
        "record_type:HumanObservaton^20",
        "record_type:Observation^20",
        "-record_type:PreservedSpecimen^20"
        // "datasetID:dr130^10"   // Example dataset boost
]
// The map of rank ids that deserve an image and what to use to find that image
imageRanks = [
    //[ rank: "kingdom", idField: "kingdomID.p", nameField: "kingdom" ],
    [ rank: "phylum", idField: null, nameField: "phylum" ],
    [ rank: "class", idField: null, nameField: "class" ],
    [ rank: "order", idField: null, nameField: "order" ],
    [ rank: "family", idField: null, nameField: "family" ],
    [ rank: "genus", idField: "genus_guid", nameField: "genus" ],
    [ rank: "species", idField: "species_guid", nameField: "taxon_name" ]
    //[ rank: "subspecies", idField: "subspecies_guid", nameField: "subspecies.p" ]
]

nationalSpeciesDatasets = "" // "dr2699,dr2700,dr2702,dr2704,dr2703,dr3118"

// SOLR additional params
solr {
    qf = "doc_name^100+text"
    bq = "taxonomicStatus:accepted^1000+rankID:7000^1000"
    defType = "edismax"
    qAlt = "text:*"
    hl = "true&hl=true&hl.fl=*&hl.simple.pre=<b>&hl.simple.post=</b>"
}
skin.layout = "main"
skin.orgNameLong = "Atlas of Living Australia"

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.use.accept.header = true
grails.mime.disable.accept.header.userAgents = []
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}

grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

// log4j configuration
def loggingDir = (System.getProperty('catalina.base') ? System.getProperty('catalina.base') + '/logs' : './logs')

log4j = {
    appenders {
        environments {
            production {
                println "BIE index logs will be written to : ${loggingDir}"
                rollingFile name: "tomcatLog", maxFileSize: '1MB', file: "${loggingDir}/${appName}.log", threshold: Level.INFO, layout: pattern(conversionPattern: "%d %-5p [%c{1}] %m%n")
            }
            development {
                console name: "stdout", layout: pattern(conversionPattern: "%d %-5p [%c{1}] %m%n"), threshold: Level.DEBUG
            }
            test {
                console name: "stdout", layout: pattern(conversionPattern: "%d %-5p [%c{1}] %m%n"), threshold: Level.FATAL
            }
        }
    }
    root {
        // change the root logger to my tomcatLog file
        error 'tomcatLog'
        warn 'tomcatLog'
        additivity = true
    }

    error   'au.org.ala.cas.client',
            "au.org.ala",
            'grails.spring.BeanBuilder',
            'grails.plugin.webxml',
            'grails.plugin.cache.web.filter',
            'grails.app.services.org.grails.plugin.resource',
            'grails.app.taglib.org.grails.plugin.resource',
            'grails.app.resourceMappers.org.grails.plugin.resource'

    debug   "grails.app",
            "au.org.ala"
}