import React, { useState, useEffect } from 'react'
import KnowledgeBaseFileList from './knowledgeBaseFileList'
import KnowledgeBaseFileDetail from './knowledgeBaseFileDetail'

import Retrival from './retrival'

const KnowledgeBaseFile: React.FC = ({ chatUIProps }) => {

  const [currentContent, setCurrentContent] = useState([])
  const [knowledgeBase, setKnowledgeBase] = useState('')

  useEffect(() => {
    console.log(chatUIProps)
    setKnowledgeBase(chatUIProps.aiNo)
  })

  return (
    <div>
      <Retrival
        chatUIProps={chatUIProps}
        knowledgeBaseNo={knowledgeBase}
      />
    </div>
  )
}

export default KnowledgeBaseFile
