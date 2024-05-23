import * as icons from "@ant-design/icons";
import type { TabsProps } from 'antd';
import ReactDOMServer from 'react-dom/server';

const useSelectIconHooks=(chooseFun:any)=>{
  //根据ant v5的icon全部更新图标以及用法，
  const directionIcons = [
    <icons.StepBackwardOutlined />,
    <icons.StepForwardOutlined />,
    <icons.FastBackwardOutlined />,
    <icons.FastForwardOutlined />,
    <icons.ShrinkOutlined />,
    <icons.ArrowsAltOutlined />,
    <icons.DownOutlined />,
    <icons.UpOutlined />,
    <icons.LeftOutlined />,
    <icons.RightOutlined />,
    <icons.CaretUpOutlined />,
    <icons.CaretDownOutlined />,
    <icons.CaretLeftOutlined />,
    <icons.CaretRightOutlined />,
    <icons.UpCircleOutlined />,
    <icons.DownCircleOutlined />,
    <icons.LeftCircleOutlined />,
    <icons.RightCircleOutlined />,
    <icons.DoubleRightOutlined />,
    <icons.DoubleLeftOutlined />,
    <icons.VerticalLeftOutlined />,
    <icons.VerticalRightOutlined />,
    <icons.VerticalAlignTopOutlined />,
    <icons.VerticalAlignMiddleOutlined />,
    <icons.VerticalAlignBottomOutlined />,
    <icons.ForwardOutlined />,
    <icons.BackwardOutlined />,
    <icons.RollbackOutlined />,
    <icons.EnterOutlined />,
    <icons.RetweetOutlined />,
    <icons.SwapOutlined />,
    <icons.SwapLeftOutlined />,
    <icons.SwapRightOutlined />,
    <icons.ArrowUpOutlined />,
    <icons.ArrowDownOutlined />,
    <icons.ArrowLeftOutlined />,
    <icons.ArrowRightOutlined />,
    <icons.PlayCircleOutlined />,
    <icons.UpSquareOutlined />,
    <icons.DownSquareOutlined />,
    <icons.LeftSquareOutlined />,
    <icons.RightSquareOutlined />,
    <icons.LoginOutlined />,
    <icons.LogoutOutlined />,
    <icons.MenuFoldOutlined />,
    <icons.MenuUnfoldOutlined />,
    <icons.BorderBottomOutlined />,
    <icons.BorderHorizontalOutlined />,
    <icons.BorderInnerOutlined />,
    <icons.BorderOuterOutlined />,
    <icons.BorderLeftOutlined />,
    <icons.BorderRightOutlined />,
    <icons.BorderTopOutlined />,
    <icons.BorderVerticleOutlined />,
    <icons.PicCenterOutlined />,
    <icons.PicLeftOutlined />,
    <icons.PicRightOutlined />,
    <icons.RadiusBottomleftOutlined />,
    <icons.RadiusBottomrightOutlined />,
    <icons.RadiusUpleftOutlined />,
    <icons.RadiusUprightOutlined />,
    <icons.FullscreenOutlined />,
    <icons.FullscreenExitOutlined />
  ];
  const suggestionIcons = [
    <icons.QuestionOutlined />,
    <icons.QuestionCircleOutlined />,
    <icons.PlusOutlined />,
    <icons.PlusCircleOutlined />,
    <icons.PauseOutlined />,
    <icons.PauseCircleOutlined />,
    <icons.MinusOutlined />,
    <icons.MinusCircleOutlined />,
    <icons.PlusSquareOutlined />,
    <icons.MinusSquareOutlined />,
    <icons.InfoOutlined />,
    <icons.InfoCircleOutlined />,
    <icons.ExclamationOutlined />,
    <icons.ExclamationCircleOutlined />,
    <icons.CloseOutlined />,
    <icons.CloseCircleOutlined />,
    <icons.CloseSquareOutlined />,
    <icons.CheckOutlined />,
    <icons.CheckCircleOutlined />,
    <icons.CheckSquareOutlined />,
    <icons.ClockCircleOutlined />,
    <icons.WarningOutlined />,
    <icons.IssuesCloseOutlined />,
    <icons.StopOutlined />
  ];
  const editIcons = [
    <icons.EditOutlined />,
    <icons.FormOutlined />,
    <icons.CopyOutlined />,
    <icons.ScissorOutlined />,
    <icons.DeleteOutlined />,
    <icons.SnippetsOutlined />,
    <icons.DiffOutlined />,
    <icons.HighlightOutlined />,
    <icons.AlignCenterOutlined />,
    <icons.AlignLeftOutlined />,
    <icons.AlignRightOutlined />,
    <icons.BgColorsOutlined />,
    <icons.BoldOutlined />,
    <icons.ItalicOutlined />,
    <icons.UnderlineOutlined />,
    <icons.StrikethroughOutlined />,
    <icons.RedoOutlined />,
    <icons.UndoOutlined />,
    <icons.ZoomInOutlined />,
    <icons.ZoomOutOutlined />,
    <icons.FontColorsOutlined />,
    <icons.FontSizeOutlined />,
    <icons.LineHeightOutlined />,
    <icons.DashOutlined />,
    <icons.SmallDashOutlined />,
    <icons.SortAscendingOutlined />,
    <icons.SortDescendingOutlined />,
    <icons.DragOutlined />,
    <icons.OrderedListOutlined />,
    <icons.UnorderedListOutlined />,
    <icons.RadiusSettingOutlined />,
    <icons.ColumnWidthOutlined />,
    <icons.ColumnHeightOutlined />
  ];
  const dataIcons=[
    <icons.AreaChartOutlined />,
    <icons.PieChartOutlined />,
    <icons.BarChartOutlined />,
    <icons.DotChartOutlined />,
    <icons.LineChartOutlined />,
    <icons.RadarChartOutlined />,
    <icons.HeatMapOutlined />,
    <icons.FallOutlined />,
    <icons.RiseOutlined />,
    <icons.StockOutlined />,
    <icons.BoxPlotOutlined />,
    <icons.FundOutlined />,
    <icons.SlidersOutlined />
  ]
  const webIcons = [
    <icons.AccountBookOutlined />,
    <icons.AimOutlined />,
    <icons.AlertOutlined />,
    <icons.ApartmentOutlined />,
    <icons.ApiOutlined />,
    <icons.AppstoreAddOutlined />,
    <icons.AppstoreOutlined />,
    <icons.AudioOutlined />,
    <icons.AudioMutedOutlined />,
    <icons.AuditOutlined />,
    <icons.BankOutlined />,
    <icons.BarcodeOutlined />,
    <icons.BarsOutlined />,
    <icons.BellOutlined />,
    <icons.BlockOutlined />,
    <icons.BookOutlined />,
    <icons.BorderOutlined />,
    <icons.BorderlessTableOutlined />,
    <icons.BranchesOutlined />,
    <icons.BugOutlined />,
    <icons.BuildOutlined />,
    <icons.BulbOutlined />,
    <icons.CalculatorOutlined />,
    <icons.CalendarOutlined />,
    <icons.CameraOutlined />,
    <icons.CarOutlined />,
    <icons.CarryOutOutlined />,
    <icons.CiCircleOutlined />,
    <icons.CiOutlined />,
    <icons.ClearOutlined />,
    <icons.CloudDownloadOutlined />,
    <icons.CloudOutlined />,
    <icons.CloudServerOutlined />,
    <icons.CloudSyncOutlined />,
    <icons.CloudUploadOutlined />,
    <icons.ClusterOutlined />,
    <icons.CodeOutlined />,
    <icons.CoffeeOutlined />,
    <icons.CommentOutlined />,
    <icons.CompassOutlined />,
    <icons.CompressOutlined />,
    <icons.ConsoleSqlOutlined />,
    <icons.ContactsOutlined />,
    <icons.ContainerOutlined />,
    <icons.ControlOutlined />,
    <icons.CopyrightOutlined />,
    <icons.CreditCardOutlined />,
    <icons.CrownOutlined />,
    <icons.CustomerServiceOutlined />,
    <icons.DashboardOutlined />,
    <icons.DatabaseOutlined />,
    <icons.DeleteColumnOutlined />,
    <icons.DeleteRowOutlined />,
    <icons.DeliveredProcedureOutlined />,
    <icons.DeploymentUnitOutlined />,
    <icons.DesktopOutlined />,
    <icons.DisconnectOutlined />,
    <icons.DislikeOutlined />,
    <icons.DollarOutlined />,
    <icons.DownloadOutlined />,
    <icons.EllipsisOutlined />,
    <icons.EnvironmentOutlined />,
    <icons.EuroCircleOutlined />,
    <icons.EuroOutlined />,
    <icons.ExceptionOutlined />,
    <icons.ExpandAltOutlined />,
    <icons.ExpandOutlined />,
    <icons.ExperimentOutlined />,
    <icons.ExportOutlined />,
    <icons.EyeOutlined />,
    <icons.EyeInvisibleOutlined />,
    <icons.FieldBinaryOutlined />,
    <icons.FieldNumberOutlined />,
    <icons.FieldStringOutlined />,
    <icons.FieldTimeOutlined />,
    <icons.FileAddOutlined />,
    <icons.FileDoneOutlined />,
    <icons.FileExcelOutlined />,
    <icons.FileExclamationOutlined />,
    <icons.FileOutlined />,
    <icons.FileGifOutlined />,
    <icons.FileImageOutlined />,
    <icons.FileJpgOutlined />,
    <icons.FileMarkdownOutlined />,
    <icons.FilePdfOutlined />,
    <icons.FilePptOutlined />,
    <icons.FileProtectOutlined />,
    <icons.FileSearchOutlined />,
    <icons.FileSyncOutlined />,
    <icons.FileTextOutlined />,
    <icons.FileUnknownOutlined />,
    <icons.FileWordOutlined />,
    <icons.FileZipOutlined />,
    <icons.FilterOutlined />,
    <icons.FireOutlined />,
    <icons.FlagOutlined />,
    <icons.FolderAddOutlined />,
    <icons.FolderOutlined />,
    <icons.FolderOpenOutlined />,
    <icons.FolderViewOutlined />,
    <icons.ForkOutlined />,
    <icons.FormatPainterOutlined />,
    <icons.FrownOutlined />,
    <icons.FunctionOutlined />,
    <icons.FundProjectionScreenOutlined />,
    <icons.FundViewOutlined />,
    <icons.FunnelPlotOutlined />,
    <icons.GatewayOutlined />,
    <icons.GifOutlined />,
    <icons.GiftOutlined />,
    <icons.GoldOutlined />,
    <icons.GroupOutlined />,
    <icons.HddOutlined />,
    <icons.HeartOutlined />,
    <icons.HistoryOutlined />,
    <icons.HolderOutlined />,
    <icons.HomeOutlined />,
    <icons.HourglassOutlined />,
    <icons.IdcardOutlined />,
    <icons.ImportOutlined />,
    <icons.InboxOutlined />,
    <icons.InsertRowAboveOutlined />,
    <icons.InsertRowBelowOutlined />,
    <icons.InsertRowLeftOutlined />,
    <icons.InsertRowRightOutlined />,
    <icons.InsuranceOutlined />,
    <icons.InteractionOutlined />,
    <icons.KeyOutlined />,
    <icons.LaptopOutlined />,
    <icons.LayoutOutlined />,
    <icons.LikeOutlined />,
    <icons.LineOutlined />,
    <icons.LinkOutlined />,
    <icons.Loading3QuartersOutlined />,
    <icons.LoadingOutlined />,
    <icons.LockOutlined />,
    <icons.MacCommandOutlined />,
    <icons.MailOutlined />,
    <icons.ManOutlined />,
    <icons.MedicineBoxOutlined />,
    <icons.MehOutlined />,
    <icons.MenuOutlined />,
    <icons.MergeCellsOutlined />,
    <icons.MergeOutlined />,
    <icons.MessageOutlined />,
    <icons.MobileOutlined />,
    <icons.MoneyCollectOutlined />,
    <icons.MonitorOutlined />,
    <icons.MoonOutlined />,
    <icons.MoreOutlined />,
    <icons.MutedOutlined />,
    <icons.NodeCollapseOutlined />,
    <icons.NodeExpandOutlined />,
    <icons.NodeIndexOutlined />,
    <icons.NotificationOutlined />,
    <icons.NumberOutlined />,
    <icons.OneToOneOutlined />,
    <icons.PaperClipOutlined />,
    <icons.PartitionOutlined />,
    <icons.PayCircleOutlined />,
    <icons.PercentageOutlined />,
    <icons.PhoneOutlined />,
    <icons.PictureOutlined />,
    <icons.PlaySquareOutlined />,
    <icons.PoundCircleOutlined />,
    <icons.PoundOutlined />,
    <icons.PoweroffOutlined />,
    <icons.PrinterOutlined />,
    <icons.ProductOutlined />,
    <icons.ProfileOutlined />,
    <icons.ProjectOutlined />,
    <icons.PropertySafetyOutlined />,
    <icons.PullRequestOutlined />,
    <icons.PushpinOutlined />,
    <icons.QrcodeOutlined />,
    <icons.ReadOutlined />,
    <icons.ReconciliationOutlined />,
    <icons.RedEnvelopeOutlined />,
    <icons.ReloadOutlined />,
    <icons.RestOutlined />,
    <icons.RobotOutlined />,
    <icons.RocketOutlined />,
    <icons.RotateLeftOutlined />,
    <icons.RotateRightOutlined />,
    <icons.SafetyCertificateOutlined />,
    <icons.SafetyOutlined />,
    <icons.SaveOutlined />,
    <icons.ScanOutlined />,
    <icons.ScheduleOutlined />,
    <icons.SearchOutlined />,
    <icons.SecurityScanOutlined />,
    <icons.SelectOutlined />,
    <icons.SendOutlined />,
    <icons.SettingOutlined />,
    <icons.ShakeOutlined />,
    <icons.ShareAltOutlined />,
    <icons.ShopOutlined />,
    <icons.ShoppingCartOutlined />,
    <icons.ShoppingOutlined />,
    <icons.SignatureOutlined />,
    <icons.SisternodeOutlined />,
    <icons.SkinOutlined />,
    <icons.SmileOutlined />,
    <icons.SolutionOutlined />,
    <icons.SoundOutlined />,
    <icons.SplitCellsOutlined />,
    <icons.StarOutlined />,
    <icons.SubnodeOutlined />,
    <icons.SunOutlined />,
    <icons.SwitcherOutlined />,
    <icons.SyncOutlined />,
    <icons.TableOutlined />,
    <icons.TabletOutlined />,
    <icons.TagOutlined />,
    <icons.TagsOutlined />,
    <icons.TeamOutlined />,
    <icons.ThunderboltOutlined />,
    <icons.ToTopOutlined />,
    <icons.ToolOutlined />,
    <icons.TrademarkCircleOutlined />,
    <icons.TrademarkOutlined />,
    <icons.TransactionOutlined />,
    <icons.TranslationOutlined />,
    <icons.TrophyOutlined />,
    <icons.TruckOutlined />,
    <icons.UngroupOutlined />,
    <icons.UnlockOutlined />,
    <icons.UploadOutlined />,
    <icons.UsbOutlined />,
    <icons.UserAddOutlined />,
    <icons.UserDeleteOutlined />,
    <icons.UserOutlined />,
    <icons.UserSwitchOutlined />,
    <icons.UsergroupAddOutlined />,
    <icons.UsergroupDeleteOutlined />,
    <icons.VerifiedOutlined />,
    <icons.VideoCameraAddOutlined />,
    <icons.VideoCameraOutlined />,
    <icons.WalletOutlined />,
    <icons.WifiOutlined />,
    <icons.WomanOutlined />
  
  ];
  //把组件<StepBackwardOutlined/>ReactDOMServer.renderToString(item)变成包含 JSX 
  const items: TabsProps["items"] = [
    {
      key: "1",
      label: "方向性图标",
      children: (
        <ul className="icon-list-ul">
          {directionIcons.map((item, index) => (
            <li key={index} onClick={() => processIcon(ReactDOMServer.renderToString(item))}>
              {item}
            </li>
          ))}
        </ul>
      ),
    },
    {
      key: "2",
      label: "指示性图标",
      children: (
        <ul className="icon-list-ul">
          {suggestionIcons.map((item, index) => (
            <li key={index} onClick={() => processIcon(ReactDOMServer.renderToString(item))}>
              {/* <IconFont
                type={item}
                className={activeIndex === item ? `active` : ``}
                onClick={() => chooseIcon(item)}
              /> */}
              {item}
            </li>
          ))}
        </ul>
      ),
    },
    {
      key: "3",
      label: "编辑类图标",
      children: (
        <ul className="icon-list-ul">
          {editIcons.map((item, index) => (
            <li key={index} onClick={() => processIcon(ReactDOMServer.renderToString(item))}>
              {/* <IconFont
                type={item}
                className={
                  activeIndex === item ? `active` : ``
                }
                onClick={() => chooseIcon(item)}
              /> */}
              {item}
            </li>
          ))}
        </ul>
      ),
    },
    {
      key: "4",
      label: "数据类图标",
      children: (
        <ul className="icon-list-ul">
          {dataIcons.map((item, index) => (
            <li key={index} onClick={() => processIcon(ReactDOMServer.renderToString(item))}>
              {/* <IconFont
                type={item}
                className={activeIndex === item ? `active` : ``}
                onClick={() => chooseIcon(item)}
              /> */}
              {item}
            </li>
          ))}
        </ul>
      ),
    },
    {
      key: "5",
      label: "网站通用图标",
      children: (
        <ul className="icon-list-ul">
          {webIcons.map((item, index) => (
            <li key={index} onClick={() => processIcon(ReactDOMServer.renderToString(item))}>
              {/* <IconFont
                type={item}
                className={activeIndex === item ? `active` : ``}
                onClick={() => chooseIcon(item)}
              /> */}
              {item}
            </li>
          ))}
        </ul>
      ),
    },
  ];


  //处理jsx字符串找data-icon="step-backward"进行处理最后resultString为StepBackwardOutlined字符串
  const processIcon = (item: any) => {
    let iconNameMatch = item.match(/data-icon="([^"]+)"/);
    let iconName = iconNameMatch ? iconNameMatch[1] : 'share-alt';

    const words = iconName.split('-');
    const capitalizedWords = words.map((word: any) => word.charAt(0).toUpperCase() + word.slice(1));
    const resultString = capitalizedWords.join('') + "Outlined";
    chooseFun(resultString)
  }
   return items
}
export default useSelectIconHooks
