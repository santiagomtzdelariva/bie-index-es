package au.org.ala.bie

import au.org.ala.bie.search.SearchResultsDTO
import grails.converters.JSON
import grails.converters.XML

/**
 * A set of JSON based search web services.
 */
class SearchController {

    def grailsApplication

    def searchService, solrSearchService, autoCompleteService, downloadService

    static defaultAction = "search"

    /**
     * Retrieve a classification for the supplied taxon.
     *
     * @return
     */
    def classification(){
        if(!params.id){
            response.sendError(404, "Please provide a GUID")
            return null
        }
        def classification = searchService.getClassification(params.id)
        render (classification as JSON)
    }

    /**
     * Returns taxa with images.
     *
     * @return
     */
    def imageSearch(){
        asJson ([searchResults:searchService.imageSearch(params.id, params.start, params.rows, params.qc)])
    }

    /**
     * Retrieves child concepts for the supplied taxon ID
     *
     * @return
     */
    def childConcepts(){
        if(!params.id){
            response.sendError(404, "Please provide a GUID")
            return null
        }
        render (searchService.getChildConcepts(params.id, request.queryString) as JSON)
    }

    def guid(){
        if(params.name == 'favicon') return; //not sure why this is happening....
        if(!params.name){
            response.sendError(404, "Please provide a name for lookups")
            return null
        }
        def model = searchService.getProfileForName(params.name)
        if(!model){
            response.sendError(404,"Name not recognised ${params.name}")
            return null
        } else {
            render (model as JSON)
        }
    }

    def shortProfile(){
        if(params.id == 'favicon') return; //not sure why this is happening....
        if(!params.id){
            response.sendError(404, "Please provide a GUID")
            return null
        }
        def model = searchService.getShortProfile(params.id)
        if(!model){
            response.sendError(404,"GUID not recognised ${params.id}")
            return null
        } else {
            asJson model
        }
    }

    def getSpeciesForNames() {
        def result = params.list('q').collectEntries { [(it): searchService.getProfileForName(it) ] } ?: null
        if (!result)
            respond result
        render result as JSON
     }

    def bulkGuidLookup(){
        def guidList = request.JSON
        def results = searchService.getTaxa(guidList)
        if(!results){
            response.sendError(404,"GUID not recognised ${params.id}")
            return null
        } else {
            def dto = [searchDTOList: results]
            asJson dto
        }
    }

    /**
     * Retrieves a profile for a taxon.
     *
     * @return
     */
    def taxon(){
        def guid = params.id
        if(guid == 'favicon') return; //not sure why this is happening....
        if(!guid){
            response.sendError(404, "Please provide a GUID")
            return null
        }
        def model = searchService.getTaxon(guid)
        log.debug "taxon model = ${model}"

        if(!model) {
            response.sendError(404, "GUID not recognised ${guid}")
            return null
        } else if (model.taxonConcept?.guid && model.taxonConcept.guid != guid) {
            // old identifier so redirect to current taxon page
            redirect(action: "taxon", params:[id: model.taxonConcept.guid], permanent: true)
        } else {
            asJson model
        }
    }

    def speciesLookupBulk() {
        final req = request.getJSON()
        if (!req) {
            response.sendError(400, "Body could not be parsed or was empty")
        }
        boolean includeVernacular = req['vernacular'] ?: false
        List<String> guids = req['names']

        def result = guids.collect { guid ->
            //Need to sort the scores descended to get the highest score first
            SearchResultsDTO results = solrSearchService.findByScientificName(guid, null, 0, 1, "score", "desc", true, includeVernacular);

            // TODO repoUrlUtils.fixRepoUrls(results)
            results.getTotalRecords() > 0 ? results.searchResults.first() : null
        }
        render result as JSON
    }

    def download(){
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Pragma", "must-revalidate");
        response.setHeader("Content-Disposition", "attachment;filename=species.csv");
        response.setContentType("text/csv");
        downloadService.download(request.queryString, params.q, response.outputStream)
    }

    /**
     * Auto complete search service.
     *
     * @return
     */
    def auto(){
        log.debug("auto called with q = " + params.q)
        def autoCompleteList = autoCompleteService.auto(params.q, request.queryString)
        def payload = [autoCompleteList:autoCompleteList]
        asJson payload
    }

    /**
     * Main search across the entire index.
     *
     * @return
     */
    def search(){
        try {
            def facets = []
            def requestFacets = params.getList("facets")
            if(requestFacets){
                requestFacets.each {
                    it.split(",").each { facet -> facets << facet }
                }
            }
            asJson([searchResults: searchService.search(params.q, params, facets)])
        } catch (Exception e){
            log.error(e.getMessage(), e)
            render(["error": e.getMessage(), indexServer: grailsApplication.config.indexLiveBaseUrl] as JSON)
        }
    }

    def habitats(){
        asJson([searchResults: searchService.getHabitats()])
    }

    def habitatTree(){
        asJson([searchResults: searchService.getHabitatsTree()])
    }

    def getHabitat(){
        asJson([searchResults: searchService.getHabitatByGuid(params.guid)])
    }

    def getHabitatIDs(){
        asJson([searchResults: searchService.getHabitatsIDsByGuid(params.guid)])
    }


    private def asJson = { model ->
        response.setContentType("application/json;charset=UTF-8")
        model
    }
}