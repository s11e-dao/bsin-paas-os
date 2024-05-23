//自定义的输入框内容。不适用于这个。在每次发送请求的时候，都会重新刷新整个空间，导致声明的文本框无法获取到对应的ref映射，从而在对话的时候无法获取到输入框焦点
const formRef = React.createRef()
const inputRef = React.useRef(null)
function inputDiv() {
  return (
    <div style={{ display: 'flex' }}>
      {/*<Input style={{display:'flex'}} className={styles.footInput} placeholder={'有问题，找小财'} onChange ={event => handleInputName(event)} />*/}
      {/*<Button onClick={()=>onCommit()}>提交</Button>*/}
      <Form ref={formRef} name="basic" className={style.InputForm}>
        <Form.Item
          name="info"
          className={style.InputForm}
          style={{ marginBottom: 0 }}
        >
          <Search
            ref={inputRef}
            className={style.InputForm}
            placeholder="请输入员工姓名或邮箱前缀"
            enterButton="发送"
            onSearch={onCommit}
            onChange={(value) => handleInputName(value.target.value)}
            // className={styles.searchTop}
          />
        </Form.Item>
      </Form>
    </div>
  )
}
const handleInputName = (value) => {
  //输入过程中变化的信息时展示的，
  console.log('输入的信息=', value)
  // setInputValue(value);
}
const onCommit = (value) => {
  //点击回车时调用的
  handleSend('text', '123')
  console.log('1')
  // console.log('value输入框的信息的是=',value);
  // formRef.current.resetFields();//重置输入框
  // console.log('输入框自己的 rel==inputRef==',inputRef);
}
