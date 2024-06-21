import React, { useState } from 'react'
import KnowledgeBaseFileList from './knowledgeBaseFileList'
import KnowledgeBaseFileDetail from './knowledgeBaseFileDetail'

const KnowledgeBaseFile: React.FC = ({ chatUIProps }) => {
  const [currentContent, setCurrentContent] = useState('knowledgeBaseFileList')
  const [knowledgeBaseFileRecord, setKnowledgeBaseFileRecord] = useState(null)

  const routeChange = (record, content) => {
    console.log(record)
    console.log(content)
    setKnowledgeBaseFileRecord(record)
    setCurrentContent(content)
  }

  const Conent = () => {
    let conentComp = (
      <KnowledgeBaseFileList
        routeChange={routeChange}
        chatUIProps={chatUIProps}
      />
    )
    if (currentContent == 'knowledgeBaseFileDetail') {
      conentComp = (
        <KnowledgeBaseFileDetail
          routeChange={routeChange}
          knowledgeBaseFileRecord={knowledgeBaseFileRecord}
        />
      )
    }
    return <>{conentComp}</>
  }

  return (
    <div>
      <Conent />
    </div>
  )
}

export default KnowledgeBaseFile
