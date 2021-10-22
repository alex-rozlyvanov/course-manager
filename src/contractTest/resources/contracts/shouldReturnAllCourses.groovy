package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'should return all courses'
    request {
        method GET()
        url('/api/manager/courses') {
            headers {
                header('Authorization', execute('authToken()'))
            }
        }
    }
    response {
        body([$(
                "id": anyUuid(),
                "title": anyNonBlankString(),
                "instructors": [$(
                        "id": anyUuid()
                )]
        )])
        status 200
    }
}
