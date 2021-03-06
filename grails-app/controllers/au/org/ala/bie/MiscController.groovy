package au.org.ala.bie

import static org.codehaus.groovy.grails.web.servlet.HttpHeaders.CONTENT_DISPOSITION
import static org.codehaus.groovy.grails.web.servlet.HttpHeaders.LAST_MODIFIED

class MiscController {

    def speciesGroupService

    def speciesGroups() {
        try {
            def details = speciesGroupService.configFileDetails()
            details.is.withStream { is ->
                response.contentLength = details.size
                response.contentType = 'application/json'
                response.setHeader(CONTENT_DISPOSITION, "attachment; filename=subgroups.json")
                response.setDateHeader(LAST_MODIFIED, details.lastModified)
                response.outputStream << is
            }
        } catch (FileNotFoundException e) {
            response.sendError(404)
        }
        return
    }
}
